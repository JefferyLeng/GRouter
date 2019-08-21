package cc.guider.architeature.grouter.api;

import java.util.Map;

/**
 * 路由组Group加载数据接口
 */
public interface GRouterLoadGroup {

    /**
     * 加载路由组Group数据
     * 比如："app", GRouter$$Path$$app.class（实现了GRouterLoadPath接口）
     *
     * @return key:"app", value:"app"e分组对应的路由详细对象类
     */
    Map<String,Class<? extends GRouterLoadPath>> loadGroup();
}
