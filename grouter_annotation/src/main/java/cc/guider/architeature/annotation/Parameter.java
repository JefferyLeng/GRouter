package cc.guider.architeature.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由框架 参数跳转 参数传递 注解
 * @author JefferyLeng
 * @date 2019-08-08
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Parameter {

    String name() default "";
}
