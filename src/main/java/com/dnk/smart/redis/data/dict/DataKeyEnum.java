package com.dnk.smart.redis.data.dict;

import com.dnk.smart.redis.data.rw.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * redis server data key
 */
@RequiredArgsConstructor
@Getter
public enum DataKeyEnum {
    UDP_V1_SESSION("UV1S", UdpSessionData.class),
    UDP_V2_SESSION("UV2S", UdpSessionData.class),
    TCP_SESSION("TS", TcpSessionData.class),

    TCP_SERVER_REGISTER("TSR", TcpServerData.class),
    WEB_SERVER_REGISTER("WSR", WebServerData.class),

    COMMAND_V1_QUEUE("CV1Q", CommandData.class),
    COMMAND_V2_QUEUE("CV2Q", CommandData.class);//key,list

    private final String key;
    private final Class<?> clazz;
}
