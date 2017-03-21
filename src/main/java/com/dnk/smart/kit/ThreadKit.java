package com.dnk.smart.kit;

public class ThreadKit {
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
