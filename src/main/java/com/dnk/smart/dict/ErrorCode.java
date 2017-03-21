package com.dnk.smart.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 与网关通讯时的错误码
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    CORRECT(0, "正确"),
    UNKNOWN(101, "未知错误"),
    PROTOCOL(102, "协议格式错误"),
    PARAMETER(103, "传递参数错误"),
    ABSENT(104, "操作对象不存在"),
    EXIST(105, "操作对象已存在"),
    UNREADY(106, "未准备就绪"),
    PERMISSION(107, "无操作权限"),
    OFFLINE(108, "设备处于离线状态");

    private final int code;
    private final String description;

}
