package cc.guider.architeature.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.print.attribute.standard.MediaSize;

import cc.guider.architeature.annotation.Parameter;
import cc.guider.architeature.compiler.utils.Consts;
import cc.guider.architeature.compiler.utils.EmptyUtil;

/**
 * @author JefferyLeng
 * @Parameter注解处理器
 * @date 2019-08-08
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({Consts.ANNOTATION_TYPE_PARAMETER})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParameterProcessor extends BaseProcessor {

    /**
     * key:类节点, value:被@Parameter注解的属性集合
     */
    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    /**
     * 获取元素接口信息（生成类文件的需要的接口实现类）
     */
    TypeMirror bizCallMirror;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            //不存在@Parameter注解的类元素
            if (EmptyUtil.isEmpty(set)) {
                return false;
            }
            //获取所有被 @Parameter 注解的 元素（属性）集合
            Set<? extends Element> parameterSet = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
            if (EmptyUtil.isEmpty(parameterSet)) {
                return false;
            }
            for (Element element : parameterSet) {
                //获取上一级（父级）节点元素 注解在属性之上，父级节点就是class
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                if (tempParameterMap.containsKey(enclosingElement)) {
                    tempParameterMap.get(enclosingElement).add(element);
                } else {
                    List<Element> elementList = new ArrayList<>();
                    elementList.add(element);
                    tempParameterMap.put(enclosingElement, elementList);
                }
            }
            generateParameterFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * JavaPoet生成 parameter file
     */
    private void generateParameterFile() throws IOException {
        if (EmptyUtil.isEmpty(tempParameterMap)) {
            return;
        }
        bizCallMirror = elementUtils.getTypeElement(Consts.TYPE_GROUTER_BIZCALL).asType();
        TypeElement activityType = elementUtils.getTypeElement(Consts.TYPE_ACTIVITY);
        //获取到ParameterLoad接口类型
        TypeElement parameterType = elementUtils.getTypeElement(Consts.TYPE_GROUTER_PARAMTER_PATH);
        // 参数体配置 (Object target)
        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, Consts.PARAMETER_ARGUMENTS_NAME).build();

        for (Map.Entry<TypeElement, List<Element>> typeElementListEntry : tempParameterMap.entrySet()) {
            TypeElement keyType = typeElementListEntry.getKey();
            if (!typeUtils.isSubtype(keyType.asType(), activityType.asType())) {
                throw new RuntimeException("@Parameter注解目前仅作用于Activity之上");
            }
            //获取类名
            ClassName className = ClassName.get(keyType);

            //构建方法体： public void loadParameter(Object target)
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Consts.PARAMETER_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec)
                    .addStatement("$T t = ($T)target", className, className);
            for (Element element : typeElementListEntry.getValue()) {
                //遍历每一个加@Parameter的属性
                addStatement(methodBuilder, element);
            }

//            String finalGenerateClassName = Consts.GENERATE_PARAMETER_CLASS_NAME_PREFIX + keyType.getSimpleName();
            String finalGenerateClassName = keyType.getSimpleName() + Consts.GENERATE_PARAMETER_CLASS_NAME_SUFFIX;
            logger.info("生成的parameter class name : " + finalGenerateClassName);

            JavaFile.builder(className.packageName(), // 声明要传递的参数可以不加public 所以保证生成的class和要传递参数的类在同一个包
                    TypeSpec.classBuilder(finalGenerateClassName)// 类名
                            .addSuperinterface(ClassName.get(parameterType))// 实现ParameterLoad接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())// 方法的构建（方法参数 + 方法体）
                            .build()
            ).build()// JavaFile构建完成
                    .writeTo(filer);// 文件生成器开始生成类文件


        }
    }

    private void addStatement(MethodSpec.Builder methodBuilder, Element element) {
        TypeMirror typeMirror = element.asType();
        //获取TypeKind 枚举类型的序列号 从而判断该参数的类型
        int type = typeMirror.getKind().ordinal();
        //获取属性名
        String fieldName = element.getSimpleName().toString();
        //获取注解的value
        String annotationValue = element.getAnnotation(Parameter.class).name();
        //developer可以选择默认的参数名 也可以指定
        annotationValue = EmptyUtil.isEmpty(annotationValue) ? fieldName : annotationValue;
        //ex: t.name
        String finaleValue = "t." + fieldName;
        StringBuilder methodContentBuilder = new StringBuilder();
        // t.name = t.getIntent().getIntExtra()
        methodContentBuilder.append(finaleValue);
        methodContentBuilder.append(" = t.getIntent().");
        if (type == TypeKind.INT.ordinal()) {
            methodContentBuilder.append("getIntExtra($S, ")
                    .append(finaleValue)
                    .append(")");
        } else if (type == TypeKind.BOOLEAN.ordinal()) {
            methodContentBuilder.append("getBooleanExtra($S, ")
                    .append(finaleValue)
                    .append(")");
        } else {
            //t.name = t.getIntent().getStringExtra("name");
            if (typeMirror.toString().equalsIgnoreCase(Consts.STRING)) {
                methodContentBuilder.append("getStringExtra($S)");
            } else if (typeUtils.isSubtype(typeMirror, bizCallMirror)) {
                // finish: 2019-08-20 跨业务接口调用 如：App需要订单提供一个res或者一个请求接口
                // t.orderDrawable = (OrderDrawable) RouterManager.getInstance().build("/order/orderDrawable").navigation(t);
                methodContentBuilder.delete(0, methodContentBuilder.length());
                methodContentBuilder.append("t.")
                        .append(fieldName)
                        .append(" = ")
                        .append("($T)$T.getInstance().build($S).navigation(t)");

                methodBuilder.addStatement(methodContentBuilder.toString(),
                        TypeName.get(typeMirror),
                        ClassName.get(Consts.ROUTER_MANAGER_PACKAGE, Consts.ROUTER_MANAGER_CLASSNAME),
                        annotationValue);
                return;
            }
        }

        //正常拼接
        if (methodContentBuilder.toString().endsWith(")")) {
            methodBuilder.addStatement(methodContentBuilder.toString(), annotationValue);
        } else {
            // TODO: 2019-08-20 扩展出其他类型支持
            logger.error("目前暂支持String,int,boolean传参");
        }

    }
}
