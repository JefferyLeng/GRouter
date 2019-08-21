package cc.guider.architeature.grouter.api;

/**
 * 参数parameter接口
 * @author JefferyLeng
 * @date 2019-08-08
 */
public interface ParameterLoad {

    /**
     * 目标对象.属性名 = getIntent().属性类型("注解值or属性名");完成赋值
     *
     * @param target 目标对象，如：MainActivity（中的某些属性）
     */
    void loadParameter(Object target);
}
