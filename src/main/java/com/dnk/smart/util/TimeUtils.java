package com.dnk.smart.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"WeakerAccess", "unused"})
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

    public static LocalDateTime fromMillisecond(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }

    public static long getMillisecond(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String format(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
