package com.dnk.smart.util;

import java.util.concurrent.TimeUnit;

public abstract class TimeUtils {

    /**
     * @param target 目标时间
     * @param offset 允许的最大偏移值
     * @param unit   偏移单位
     * @return 是否超时
     */
    public static boolean timeout(long target, long offset, TimeUnit unit) {
        long delay = System.currentTimeMillis() - target;
        return delay > unit.toMillis(offset);
    }

    public static boolean timeout(long target, long offset) {
        return timeout(target, offset, TimeUnit.MILLISECONDS);
    }

}
