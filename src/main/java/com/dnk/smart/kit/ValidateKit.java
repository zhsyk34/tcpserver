package com.dnk.smart.kit;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ValidateKit {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(String... strs) {
        for (String str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean notEmpty(String... strs) {
        return !isEmpty(strs);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean notEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean notEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean notEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public static boolean invalid(Integer key) {
        return key == null || key <= 0;
    }

    public static boolean valid(Integer key) {
        return !invalid(key);
    }

    public static boolean invalid(Long key) {
        return key == null || key <= 0;
    }

    public static boolean valid(Long key) {
        return !invalid(key);
    }

    /**
     * @param refer 参照时间
     * @param valid 自参照时间起的有效时长
     * @param unit  有效时长单位
     * @return 是否在有效时间内
     */
    public static boolean time(long refer, long valid, TimeUnit unit) {
        long delay = System.currentTimeMillis() - refer;
        return delay >= 0 && delay < unit.toMillis(valid);
    }

    public static boolean time(long refer, long valid) {
        return time(refer, valid, TimeUnit.SECONDS);
    }

}
