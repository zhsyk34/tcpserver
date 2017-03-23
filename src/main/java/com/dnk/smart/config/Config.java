package com.dnk.smart.config;

/**
 * 参数配置
 * 如无特殊说明,时间的单位均为秒
 */
public final class Config {

    //TCP服务器编号
    public static final String TCP_SERVER_ID = "T001";
    //TCP服务器默认端口
    public static final int TCP_SERVER_PORT = 15999;
    //TCP服务器最大并发连接数
    public static final int TCP_SERVER_BACKLOG = 1 << 16;
    //TCP预计并发连接数:app
    public static final int TCP_APP_COUNT_PREDICT = 1 << 12;
    //TCP预计并发连接数:gateway
    public static final int TCP_GATEWAY_COUNT_PREDICT = 1 << 14;
    //TCP连接超时时间
    public static final int TCP_CONNECT_TIMEOUT = 5;
    //TCP登录超时时间
    public static final int TCP_LOGIN_TIMEOUT = 5;
    //app单次与服务器建立连接的最大时长
    public static final int TCP_APP_TIMEOUT = 20;
    //网关单次与服务器建立连接的最大时长
    public static final int TCP_GATEWAY_TIMEOUT = 30 * 60;
    //APP请求的最长处理时间(从开始处理时计时)
    public static final int TCP_MESSAGE_TIMEOUT = 18;
    //TCP允许的最大的无效缓冲数据(byte)
    public static final int TCP_BUFFER_SIZE = 1 << 10;
    //TCP为网关UDP端口的起始有效值
    public static final int TCP_ALLOT_MIN_UDP_PORT = 50000;
    //TCP分配端口回收扫描频率(每天00:00执行)
    public static final int TCP_ALLOT_UDP_PORT_RECYCLE = 1;//天
    /**
     * -----------------------------UDP配置-----------------------------
     */
    //UDP服务器本地端口
    public static final int UDP_SERVER_PORT = 15998;

    /**
     * -----------------------------日志配置-----------------------------
     */
    public static final int LOGGER_CAPACITY = 5000;
    /**
     * -----------------------------系统时间配置-----------------------------
     */
    //服务器启动完毕后执行扫描任务
    public static final int SCHEDULE_TASK_DELAY_TIME = 1;
    //服务器启动状态监视时间间隔
    public static final int SERVER_START_MONITOR_TIME = 1500;//ms
    //通过UDP唤醒网关时检测状态时间间隔

    /**
     * -----------------------------DB配置-----------------------------
     */
    public static final int BATCH_FETCH_SIZE = 10;
    public static final int GATEWAY_AWAKE_TIMES = 3;
    public static final int GATEWAY_AWAKE_STEP = 5000;//ms
    /**
     * -----------------------------TCP配置-----------------------------
     */
    private static final String LOCAL_HOST = "127.0.0.1";
    //TCP服务器地址
    public static final String TCP_SERVER_HOST = LOCAL_HOST;
    //网关发送UDP心跳包频率
    private static final int UDP_HEART_BEAT_FREQ = 10;
    //网关心跳信息过期时间
    public static final int UDP_HEART_BEAT_DUE = UDP_HEART_BEAT_FREQ * 6;

}
