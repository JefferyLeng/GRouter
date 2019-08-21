package cc.guider.architeature.compiler;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cc.guider.architeature.compiler.utils.Consts;
import cc.guider.architeature.compiler.utils.EmptyUtil;
import cc.guider.architeature.compiler.utils.Logger;

/**
 * base 注解处理器
 * @author JefferyLeng
 * @date 2019-08-01
 */
public abstract class BaseProcessor extends AbstractProcessor {

    /**
     * 操作Element工具类 (类、函数、属性都是Element)
     */
    protected Elements elementUtils;
    /**
     * essager用来报告错误，警告和其他提示信息
     */
    protected Messager messager;
    /**
     * type(类信息)工具类，包含用于操作TypeMirror的工具方法
     */
    protected Types typeUtils;
    /**
     * 文件生成器 类/资源，Filter用来创建新的类文件，class文件以及辅助文件
     */
    protected Filer filer;
    protected Logger logger;
    /**
     * 子模块名，如：app/order/personal。需要拼接类名时用到（必传）ARouter$$Group$$order
     */
    protected String moduleName;
    /**
     * apt生成代码的路径
     */
    protected String packageNameForAPT;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
        logger = new Logger(messager);
        // 通过ProcessingEnvironment去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtil.isEmpty(options)) {
            moduleName = options.get(Consts.KEY_MODULE_NAME);
            packageNameForAPT = options.get(Consts.KEY_APT_LOCATION_NAME);
            // 有坑：Diagnostic.Kind.ERROR，异常会自动结束，不像安卓中Log.e
//            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>> " + moduleName);
//            messager.printMessage(Diagnostic.Kind.NOTE, "packageNameForAPT >>> " + packageNameForAPT);
            logger.info(moduleName);
            logger.info(packageNameForAPT);
        }

        // 必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtil.isEmpty(moduleName) || EmptyUtil.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或者packageName为空，请在对应build.gradle配置参数");
        }

    }
}
