package cc.guider.architeature.grouter.api.manager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.LruCache;

import cc.guider.architeature.grouter.api.ParameterLoad;

/**
 * 参数管理器
 *
 * @author JefferyLeng
 * @date 2019-08-20
 */
public final class ParameterManager {

    /**
     * key 类名 value :参数ParameterLoader的实现类
     */
    private LruCache<String, ParameterLoad> parameterLoadLruCache;

    private ParameterManager() {
        parameterLoadLruCache = new LruCache<>(200);
    }

    public static ParameterManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static final ParameterManager INSTANCE = new ParameterManager();
    }

    /**
     * 传入的Activity中所有被@Parameter注解的属性。通过加载APT生成源文件，并给属性赋值
     * @param activity 需要给属性赋值的类，如：MainActivity中所有被@Parameter注解的属性
     */
    public void loadParameter(@NonNull Activity activity) {
        try {
            //全类名
            String className = activity.getClass().getName();
            ParameterLoad iParameterLoad = parameterLoadLruCache.get(className);
            if (iParameterLoad == null) {
                Class<?> clazz = Class.forName(className + "$$Parameter");
                iParameterLoad = (ParameterLoad) clazz.newInstance();
                parameterLoadLruCache.put(className, iParameterLoad);
            }
            iParameterLoad.loadParameter(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
