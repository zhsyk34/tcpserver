package com.dnk.smart.dict.redis;

import com.dnk.smart.dict.redis.cache.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RedisKey {
    TCP_SESSION(TcpSessionData.class),
    UDP_V2_SESSION(UdpSessionData.class),
    TCP_SERVER_REGISTER(TcpServerData.class),
    COMMAND_V2_QUEUE(Command.class),

    /*--------------------------以下在webServer中处理--------------------------*/
    @Deprecated
    COMMAND_V1_QUEUE(Command.class),
    @Deprecated
    WEB_SERVER_REGISTER(WebServerData.class),
    @Deprecated
    UDP_V1_SESSION(UdpSessionData.class);

    @NonNull
    private final Class<?> clazz;

    public final String key() {
        return this.name().replace("_", ":").toLowerCase();
    }
}
