package com.dnk.smart.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class ConvertKit {

    public static LocalDateTime from(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    public static boolean primitive(Boolean bool) {
        return bool == null ? false : bool;
    }

    public static int primitive(Integer integer) {
        return integer == null ? -1 : integer;
    }

    public static LocalDateTime from(String str) {
        try {
            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return LocalDateTime.of(LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIN);
        }
    }

    public static long from(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String fillZero(int i, int length) {
        if (length < 1) {
            throw new RuntimeException("length must > 0.");
        }
        return String.format("%0" + length + "d", i);
    }

    public static <T> T getEnum(Class<T> clazz, int index) {
        T[] enums = clazz.getEnumConstants();
        if (enums == null) {
            return null;
        }
        if (index > -1 && index < enums.length) {
            return enums[index];
        }
        return null;
    }

    public static <T> T getEnum(Class<T> clazz, String name, boolean strict) {
        if (name == null) {
            return null;
        }
        T[] enums = clazz.getEnumConstants();
        if (enums == null) {
            return null;
        }
        if (strict) {
            for (T t : enums) {
                if (name.equals(t.toString())) {
                    return t;
                }
            }
        } else {
            for (T t : enums) {
                if (name.equalsIgnoreCase(t.toString())) {
                    return t;
                }
            }
        }

        return null;
    }

    public static <T> T getEnum(Class<T> clazz, String name) {
        return getEnum(clazz, name, true);
    }

}
