package com.dnk.smart.dict.tcp;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum State {
    ACCEPT(1, "初始连接"),
    REQUEST(2, "登录请求"),
    VERIFY(3, "身份验证"),
    AWAIT(4, "(网关)等待系统分配端口资源"),
    SUCCESS(5, "登录成功"),
    CLOSED(6, "关闭连接");

    private static final Map<Integer, State> STEP_MAP = new HashMap<>();

    static {
        for (State state : values()) {
            STEP_MAP.put(state.getStep(), state);
        }
    }

    private final int step;
    private final String description;

    public static State from(int step) {
        return STEP_MAP.get(step);
    }

    public State previous() {
        return from(this.step - 1);
    }

    public State next() {
        return from(this.step + 1);
    }

    public boolean isAfter(@NonNull State state) {
        return this.step >= state.step;
    }
}
