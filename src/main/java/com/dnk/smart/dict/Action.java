package com.dnk.smart.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令可能的action值
 * 只枚举服务端需要处理的情况
 */
@Getter
@AllArgsConstructor
public enum Action {
    /**
     * 1.登录相关
     */
    LOGIN_INFORM(1, "loginReady", "通知网关登录"),
    LOGIN_REQUEST(1, "loginReq", "登录请求"),
    LOGIN_VERIFY(1, "loginVerify", "登录验证"),

    /**
     * 2.网关心跳
     */
    HEART_BEAT(2, "cmtHeartbeat", "网关心跳"),

    /**
     * 3.网关主动推送至本服务器
     */
    UNLOCK_PUSH(3, "cmtUnlock", "网关推送开锁信息"),
    LOCK_STATUS_PUSH(3, "cmtLockStat", "提交开锁状态变化信息"),
    LOCK_RECORD_PUSH(3, "cmtLockRecord", "推送脱机开锁记录"),
    ALARM_PUSH(3, "cmtAlarm", "网关推送报警信息"),
    @Deprecated
    DEVICE_INFO_PUSH(3, "cmtDevInfo", "网关推送设备信息"),//用于手机 App 添加设备时，网关主动发送设备信息至手机 App，通过 UDP 广播传输

    /**
     * 版本信息请求,直接响应
     */
    GET_VERSION(4, "getVersion", "获取服务器上网关版本信息");

    private static final Map<String, Action> MAP = new HashMap<>();

    static {
        for (Action action : values()) {
            MAP.put(action.getName(), action);
        }
    }

    private final int type;
    @NonNull
    private final String name;
    private final String description;

    public static Action from(String name) {
        return MAP.get(name);
    }
}
