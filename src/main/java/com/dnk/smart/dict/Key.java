package com.dnk.smart.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * 指令可能存在的key值
 * 只枚举服务端需要处理的情况
 */
@Getter
@AllArgsConstructor
public enum Key {
    ACTION("action", "指令"),
    RESULT("result", "结果"),
    TYPE("clientType", "设备类型"),
    SN("devSN", "网关SN码"),
    VERSION("appVersionNo", "网关版本"),
    APPLY("UDPPort", "网关请求的UDP端口"),
    ERROR_NO("errno", "错误码"),
    KEY("key", "密钥信息"),
    KEYCODE("keyCode", "密钥值"),

    /**
     * 新增错误信息
     */
    ERROR_INFO("errInfo", "错误信息"),

    /**
     * 推送
     */
    DATA("data", "推送UDP数组数据信息");

    @NonNull
    private final String name;
    private final String description;
}
