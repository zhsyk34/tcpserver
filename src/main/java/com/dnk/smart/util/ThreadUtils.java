package com.dnk.smart.util;

public class ThreadUtils {
    /**
     * @param millis 等待毫秒值
     */
    public static void await(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
