package cc.guider.architeature.grouter.api.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import java.util.Map;

import cc.guider.architeature.annotation.model.GRouterBean;
import cc.guider.architeature.grouter.api.BizCall;
import cc.guider.architeature.grouter.api.GRouterLoadGroup;
import cc.guider.architeature.grouter.api.GRouterLoadPath;

/**
 * 路由加载及跳转管理器
 *
 * @author JefferyLeng
 * @date 2019-08-08
 */
public final class RouterManager {

    private String group;
    private String path;
    private LruCache<String, GRouterLoadGroup> groupLruCache;
    private LruCache<String, GRouterLoadPath> pathLruCache;
    private static final String GROUP_FILE_PREFIX_NAME = "GRouter$$Group$$";

    private RouterManager() {
        groupLruCache = new LruCache<>(200);
        pathLruCache = new LruCache<>(200);
    }

    private static class SingletonHolder {
        private static final RouterManager INSTANCE = new RouterManager();
    }

    public static RouterManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     *
     * @param context
     * @param bundleManager
     * @param code 可能是requestCode,也可能是resultCode,取决于isResult
     * @return 普通跳转可以忽略，用于跨模块CALL接口
     */
    public Object navigation(@NonNull Context context, BundleManager bundleManager, int code) {
        /*
            改进：阿里的路由path随意写，导致无法找到随意拼接APT生成的源文件，如：GRouter$$Group$$abc
            找不到，就加载私有目录下apk中的所有dex并遍历，获得所有包名为xxx的类。并开启了线程池工作
            这里的优化是：强制代码规范写法，准确定位GRouter$$Group$$app
         */
        String groupClassName = context.getPackageName() + ".apt." + GROUP_FILE_PREFIX_NAME + group;
        Log.d("grouter", "navigation: >>> groupClassName : " + groupClassName);
        try {
            GRouterLoadGroup gRouterLoadGroup = groupLruCache.get(groupClassName);
            if (gRouterLoadGroup == null) {
                Class<?> groupClass = Class.forName(groupClassName);
                gRouterLoadGroup = (GRouterLoadGroup) groupClass.newInstance();
                groupLruCache.put(group, gRouterLoadGroup);
            }

            if (gRouterLoadGroup.loadGroup().isEmpty()) {
                throw new RuntimeException("路由表加载失败");
            }

            GRouterLoadPath gRouterLoadPath = pathLruCache.get(path);
            if (gRouterLoadPath == null) {
                Class<? extends GRouterLoadPath> pathClass = gRouterLoadGroup.loadGroup().get(group);
                gRouterLoadPath = pathClass.newInstance();
                pathLruCache.put(path,gRouterLoadPath);
            }

            if (gRouterLoadPath != null) {
                Map<String, GRouterBean> routerBeanMap = gRouterLoadPath.loadPath();
                if (routerBeanMap.isEmpty()) {
                    throw new RuntimeException("路由地址加载失败");
                }
                GRouterBean gRouterBean = routerBeanMap.get(path);
                switch (gRouterBean.getType()) {
                    case ACTIVITY:
                        Class<?> targetClass = gRouterBean.getClazz();
                        Intent intent = new Intent(context,targetClass);
                        intent.putExtras(bundleManager.getBundle());
                        //startActivityForResult --> setResult()
                        if (bundleManager.isResult()) {
                            ((Activity)context).setResult(code,intent);
                            ((Activity)context).finish();
                            return null;
                        }
                        if (code > 0) {
                            //表示跳转需要回调
                            ((Activity)context).startActivityForResult(intent,code,bundleManager.getBundle());
                        } else {
                            context.startActivity(intent);
                        }
                        break;

                    case BIZCALL:
                        Class<?> clazz = gRouterBean.getClazz();
                        BizCall bizCall = (BizCall) clazz.newInstance();
                        bundleManager.setBizCall(bizCall);
                        return bundleManager.getBizCall();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按照规范配置,如: /app/MainActivity");
        }

        group = subGroupFromPath(path);
        this.path = path;
        return new BundleManager();
    }

    /**
     * 从传入的path中 截取到group
     *
     * @param path
     * @return
     */
    private String subGroupFromPath(String path) {
        if (path.lastIndexOf("/") == 0) {
            throw new RuntimeException("@GRouter未按照规范配置,如: /app/MainActivity");
        }

        String finalGroupName = path.substring(1, path.lastIndexOf("/"));
        if (TextUtils.isEmpty(finalGroupName)) {
            throw new RuntimeException("@GRouter未按照规范配置,如: /app/MainActivity");
        }
        return finalGroupName;
    }

}
