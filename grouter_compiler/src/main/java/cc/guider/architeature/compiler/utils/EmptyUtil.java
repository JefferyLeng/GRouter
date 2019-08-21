package cc.guider.architeature.compiler.utils;

import java.util.Collection;
import java.util.Map;

/**
 * 空判断工具 字符串，集合
 * @author JefferyLeng
 * @date 2019-08-01
 */
public class EmptyUtil {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}