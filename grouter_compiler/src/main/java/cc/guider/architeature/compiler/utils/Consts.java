package cc.guider.architeature.compiler.utils;

/**
 * constants here
 * @author JefferyLeng
 * @date 2019-08-01
 */
public interface Consts {

    String SEPARATOR = "$$";
    String PROJECT = "GRouter";
    String TAG = PROJECT + "::";
    String GENERATE_PATH_CLASS_NAME_PREFIX = PROJECT + SEPARATOR + "Path" + SEPARATOR;
    String GENERATE_GROUP_CLASS_NAME_PREFIX = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    String GENERATE_PARAMETER_CLASS_NAME_SUFFIX = "$$Parameter";

    String PREFIX_OF_LOGGER = PROJECT + "::Compiler";
    String KEY_MODULE_NAME = "GROUTER_MODULE_NAME";
    String KEY_APT_LOCATION_NAME = "APT_LOCATION_NAME";
    String GROUTER_BASE_PACKAGE = "cc.guider.architeature.grouter.api";

    String ANNOTATION_TYPE_GROUTER = "cc.guider.architeature.annotation.GRouter";

    String ANNOTATION_TYPE_PARAMETER = "cc.guider.architeature.annotation.Parameter";

    /**
     * todo:暂时只支持到Activity 可以任意扩展到Service，Fragment等 随便怎么玩儿
     */
    String TYPE_ACTIVITY = "android.app.Activity";

    /**
     * 模块间业务通信的规范接口
     */
    String TYPE_BIZCALL = GROUTER_BASE_PACKAGE + ".BizCall";

    String TYPE_GROUTER_GROUP = GROUTER_BASE_PACKAGE + ".GRouterLoadGroup";

    String TYPE_GROUTER_PATH = GROUTER_BASE_PACKAGE + ".GRouterLoadPath";

    String TYPE_GROUTER_PARAMTER_PATH = GROUTER_BASE_PACKAGE + ".ParameterLoad";

    /**
     * 跨模块业务回调接口
     */
    String TYPE_GROUTER_BIZCALL = GROUTER_BASE_PACKAGE + ".BizCall";

    /**
     * 生成path类的方法名，需要和集成的接口一致
     */
    String PATH_METHOD_NAME = "loadPath";

    String GROUP_METHOD_NAME = "loadGroup";

    String PARAMETER_METHOD_NAME = "loadParameter";

    /**
     * @Paramter 的入参参数名
     */
    String PARAMETER_ARGUMENTS_NAME = "target";

    /**
     * 生成path类 方法的变量名
     */
    String PATH_METHOD_PARAM_NAME = "pathMap";

    String GROUP_METHOD_VAR_NAME = "groupMap";

    /**
     * TypeKind不提供String的判断 so 自己来吧
     */
    String STRING = "java.lang.String";

    String ROUTER_MANAGER_PACKAGE = "cc.guider.architeature.grouter.api.manager";

    String ROUTER_MANAGER_CLASSNAME = "RouterManager";
}
