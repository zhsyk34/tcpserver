package com.dnk.smart.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrNo {

    /**
     * 网关通讯反馈
     */
    CORRECT(1, 0, "正确"),
    UNKNOWN(1, 101, "未知错误"),
    PROTOCOL(1, 102, "协议格式错误"),
    PARAMETER(1, 103, "传递参数错误"),
    ABSENT(1, 104, "操作对象不存在"),
    EXIST(1, 105, "操作对象已存在"),
    UNREADY(1, 106, "未准备就绪"),
    PERMISSION(1, 107, "无操作权限"),
    OFFLINE(1, 108, "设备处于离线状态"),

    /**
     * 新增反馈
     */
    TIMEOUT(2, 500, "超时");

    private final int type;
    private final int code;
    private final String description;

}
