package cc.guider.architeature.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cc.guider.architeature.compiler.utils.Consts;

/**
 * Parameter工厂 用于生成GRouter$$Parameter$$Main.class
 * @deprecated 暂时不同
 * @author JefferyLeng
 * @date 2019-08-19
 */
@Deprecated
public class ParameterFactory {

    /**
     * 方法内容：
     * MainActivity t = （MainActivity）target；
     */
    private static final String CONTENT = "$T t = ($T)target";

    /**
     * JavaPoet方法体构建
     */
    private MethodSpec.Builder methodBuild;

    /**
     * type(类信息) 工具类，包含用于操作TypeMirror的工具方法
     */
    private Types typeUtils;

    /**
     * 获取元素接口信息（生成类文件需要的接口实现类）
     */
    private TypeMirror callMirror;

    /**
     * 类名 如：MainActivity
     */
    private ClassName className;

    private ParameterFactory(Builder builder) {
        this.typeUtils = builder.typeUtils;
        this.className = builder.className;

        methodBuild = MethodSpec.methodBuilder(Consts.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);

        this.callMirror = builder.elementUtils
                .getTypeElement(Consts.TYPE_GROUTER_BIZCALL)
                .asType();


    }



    public static class Builder {

        private Elements elementUtils;

        private Types typeUtils;

        private ClassName className;

        /**
         * 方法体参数
         */
        private ParameterSpec parameterSpec;

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }

        public Builder setElementUtils(Elements elementUtils) {
            this.elementUtils = elementUtils;
            return this;
        }

        public Builder setTypeUtils(Types typeUtils) {
            this.typeUtils = typeUtils;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec方法参数体为空");
            }

            if (className == null) {
                throw new IllegalArgumentException("方法内容中的className为空");
            }
            return new ParameterFactory(this);
        }
    }
}
