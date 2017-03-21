package com.dnk.smart.dict;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum Result {

    OK("ok", "正确响应"),
    NO("no", "错误响应");

    @NonNull
    private final String name;
    private final String description;

    public static Result from(String name) {
        for (Result result : values()) {
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

}
