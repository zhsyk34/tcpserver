package com.dnk.smart.dict;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Result {
    OK("ok", "正确响应"),
    NO("no", "错误响应");

    private static final Map<String, Result> MAP = new HashMap<>();

    static {
        for (Result result : values()) {
            MAP.put(result.getName(), result);
        }
    }

    @NonNull
    private final String name;
    private final String description;

    public static Result from(String name) {
        return MAP.get(name);
    }
}
