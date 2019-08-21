package cc.guider.architeature.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import cc.guider.architeature.annotation.GRouter;
import cc.guider.architeature.annotation.model.GRouterBean;
import cc.guider.architeature.compiler.utils.Consts;
import cc.guider.architeature.compiler.utils.EmptyUtil;

/**
 * @author JefferyLeng
 * @GRouter 注解的处理器
 * @date 2019-08-01
 * 撸Processor就一句话：细心再细心，出了问题debug真的不好调试
 * AutoService则是固定的写法，加个注解即可
 * 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
 * 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
 */
@AutoService(Processor.class)
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Consts.ANNOTATION_TYPE_GROUTER})
public class GRouterProcessor extends BaseProcessor {

    /**
     * 临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
     * key:组名"app", value:"app"组的路由路径"ARouter$$Path$$app.class"
     */
    private Map<String, List<GRouterBean>> tempPathMap = new HashMap<>();

    /**
     * 临时map存储，用来存放路由Group信息，生成路由组类文件时遍历
     * key:组名"app", value:类名"ARouter$$Path$$app.class"
     */
    private Map<String, String> tempGroupMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //set为空 表示没有有GRouter注解的类
        if (EmptyUtil.isEmpty(set)) {
            return false;
        }
        //获取到所有GRouter注解的元素集合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(GRouter.class);
        if (EmptyUtil.isEmpty(elements)) {
            return false;
        }

        try {
            parseElements(elements);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //坑:必须返回true 表示注解被处理
        return true;
    }

    private void parseElements(Set<? extends Element> elements) throws IOException {
        TypeMirror activityMirror = elementUtils.getTypeElement(Consts.TYPE_ACTIVITY).asType();
        TypeMirror bizCallMirror = elementUtils.getTypeElement(Consts.TYPE_GROUTER_BIZCALL).asType();

        for (Element element : elements) {
            TypeMirror elementMirror = element.asType();
            logger.info("遍历的元素信息:" + elementMirror.toString());
            GRouter gRouter = element.getAnnotation(GRouter.class);
            GRouterBean gRouterBean = new GRouterBean.Builder()
                    .setPath(gRouter.path())
                    .setGroup(gRouter.group())
                    .setElement(element)
                    .build();

            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                gRouterBean.setType(GRouterBean.Type.ACTIVITY);
            } else if (typeUtils.isSubtype(elementMirror,bizCallMirror)) {
                gRouterBean.setType(GRouterBean.Type.BIZCALL);
            } else {
                throw new RuntimeException("GRouter注解目前只支持作用在Activity之上");
            }
            // 赋值临时map存储，用来存放路由组Group对应的详细Path类对象
            storageTempMap(gRouterBean);
        }

        // 获取GRouterLoadGroup、GRouterLoadPath类型（生成类文件需要实现的接口）
        TypeElement pathElement = elementUtils.getTypeElement(Consts.TYPE_GROUTER_PATH);
        TypeElement groupElement = elementUtils.getTypeElement(Consts.TYPE_GROUTER_GROUP);

        // 第一步：生成路由组Group对应详细Path类文件，如：GRouter$$Path$$Main
        createPathFile(pathElement);

        // 第二步：生成路由组Group implements GRouterPathGroup类文件 如:GRouter$$Group$$Main
        createGroupFile(groupElement, pathElement);
    }


    /**
     * 生成路由组Group对应的详细Path类
     *
     * @param pathElement GRouterLoadPath接口
     */
    private void createPathFile(TypeElement pathElement) throws IOException {
        // 判断是否有需要生成的类文件
        if (EmptyUtil.isEmpty(tempPathMap)) return;

        //Javapoet返回值 生成的path类的返回值一致
        TypeName methodReturnType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(GRouterBean.class)
        );

        //遍历分组，每一个分组创建一个路径类文件，如：GRouter$$Path$$app、GRouter$$Path$$order等
        for (Map.Entry<String, List<GRouterBean>> entry : tempPathMap.entrySet()) {

            //方法声明 public Map<String, RouterBean> loadPath() {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Consts.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturnType);

            //方法体
            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(GRouterBean.class),
                    Consts.PATH_METHOD_PARAM_NAME,
                    ClassName.get(HashMap.class)
            );

            // 一个分组，如：GRouter$$Path$$app。有很多详细路径信息，如：/app/MainActivity、/app/OtherActivity
            List<GRouterBean> pathList = entry.getValue();
            for (GRouterBean gRouterBean : pathList) {
                methodBuilder.addStatement("$N.put($S,new $T().setType($T.$L).setClazz($T.class).setGroup($S).setPath($S).build())",
                        Consts.PATH_METHOD_PARAM_NAME, //pathMap.put
                        gRouterBean.getPath(),  // /app/MainActivity
                        ClassName.get(GRouterBean.Builder.class), //new GRouter.Builder()
                        ClassName.get(GRouterBean.Type.class),  // GRouterBean.Type
                        gRouterBean.getType(),   // ACTIVITY
                        ClassName.get((TypeElement) gRouterBean.getElement()),  //MainActivity.class
                        gRouterBean.getGroup(),  //app
                        gRouterBean.getPath()   // /app/MainActivity
                );
            }

            //遍历之后 生成return代码
            methodBuilder.addStatement("return $N", Consts.PATH_METHOD_PARAM_NAME);
            String finalClassName = Consts.GENERATE_PATH_CLASS_NAME_PREFIX + entry.getKey();
            logger.info("APT生成路由Path类文件：" +
                    packageNameForAPT + "." + finalClassName);
            //生成类
            JavaFile.builder(packageNameForAPT, // 包名
                    TypeSpec.classBuilder(finalClassName)// 类名
                            .addSuperinterface(ClassName.get(pathElement))// 实现ARouterLoadPath接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())// 方法的构建（方法参数 + 方法体）
                            .build()
            ).build()// JavaFile构建完成
                    .writeTo(filer);// 文件生成器开始生成类文件

            //GRouterLoadPath生成完成 将生成的
            tempGroupMap.put(entry.getKey(), finalClassName);
        }


    }

    /**
     * 创建每个路由组对应的类
     *
     * @param groupElement
     */
    private void createGroupFile(TypeElement groupElement, TypeElement pathElement) throws IOException {
        if (EmptyUtil.isEmpty(tempGroupMap)) {
            return;
        }

        //返回类型 Map<String,Class<? extends GRouterLoadPath>
        TypeName methodRetrun = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                // 第二个参数：Class<? extends ARouterLoadPath>
                // 某某Class是否属于ARouterLoadPath接口的实现类
                ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(pathElement)))

        );

        //方法构造器
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Consts.GROUP_METHOD_NAME)
                .returns(methodRetrun)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);

        //方法体
        methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(pathElement))),
                Consts.GROUP_METHOD_VAR_NAME,
                ClassName.get(HashMap.class)
        );

        for (Map.Entry<String, String> groupEntry : tempGroupMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    Consts.GROUP_METHOD_VAR_NAME,
                    groupEntry.getKey(),
                    ClassName.get(packageNameForAPT, groupEntry.getValue())
            );
        }
        //返回值的代码声明
        methodBuilder.addStatement("return $N", Consts.GROUP_METHOD_VAR_NAME);

        String finalClassName = Consts.GENERATE_GROUP_CLASS_NAME_PREFIX + moduleName;
        //生成类
        JavaFile.builder(packageNameForAPT, // 包名
                TypeSpec.classBuilder(finalClassName)// 类名
                        .addSuperinterface(ClassName.get(groupElement))// 实现ARouterLoadPath接口
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodBuilder.build())// 方法的构建（方法参数 + 方法体）
                        .build()
        ).build()// JavaFile构建完成
                .writeTo(filer);// 文件生成器开始生成类文件

    }

    /**
     * 将所有GRouter注解 封装的bean加入到map内存缓存中
     *
     * @param gRouterBean
     */
    private void storageTempMap(GRouterBean gRouterBean) {
        if (!checkRouterPath(gRouterBean)) {
            return;
        }
        logger.info(gRouterBean.toString());
        List<GRouterBean> gRouterBeans = tempPathMap.get(gRouterBean.getGroup());
        if (EmptyUtil.isEmpty(gRouterBeans)) {
            // 如果从Map中找不到key为：bean.getGroup()的数据，就新建List集合再添加进Map
            gRouterBeans = new ArrayList<>();
            gRouterBeans.add(gRouterBean);
            tempPathMap.put(gRouterBean.getGroup(), gRouterBeans);
        } else {
            // 找到了key，直接加入List集合
            gRouterBeans.add(gRouterBean);
        }
    }

    /**
     * 强制要求按照格式来配置路由 无需遍历dex文件加载class
     * @param gRouterBean
     */
    private boolean checkRouterPath(GRouterBean gRouterBean) {
        String group = gRouterBean.getGroup();
        String path = gRouterBean.getPath();

        logger.info("checkRouterPath : group = " + group + ";path = " + path);
        // @ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (EmptyUtil.isEmpty(path) || !path.startsWith("/")) {
            logger.error("@GRouter注解中的path值，必须要以 /开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            //开发者必须遵循的注解规范
//            logger.error("@GRouter注解未按规范配置，如：/app/MainActivity");
            logger.error("@GRouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }

        // @GRouter注解中的group有赋值情况
        if (!EmptyUtil.isEmpty(group) && !group.equals(moduleName)) {
            // 开发者必须遵循的规范
            logger.error(new IllegalArgumentException("@GRouter注解中的group值必须和子模块名一致！"));
            return false;
        }
        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        logger.info("checkRouterPath : finalGroupName = " + finalGroup + "; moudleName = " + moduleName);
        if (!finalGroup.equals(moduleName)) {
            logger.error(new IllegalArgumentException("@GRouter注解中的group值必须和子模块名一致！"));
            return false;
        }
        gRouterBean.setGroup(finalGroup);
        return true;

    }
}
