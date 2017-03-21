package com.dnk.smart.tcp.cache.dict;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum Device {
    GATEWAY(0, "智能网关"),
    APP(1, "手机应用程序");

    private static final Map<Integer, Device> MAP = new HashMap<>();

    static {
        for (Device device : values()) {
            MAP.put(device.getType(), device);
        }
    }

    private final int type;
    private final String description;

    public static Device from(int type) {
        return MAP.get(type);
    }
}