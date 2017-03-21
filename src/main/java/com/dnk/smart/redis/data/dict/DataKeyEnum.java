package com.dnk.smart.redis.data.dict;

import com.dnk.smart.redis.data.rw.TcpServerData;
import com.dnk.smart.redis.data.rw.TcpSessionData;
import com.dnk.smart.redis.data.rw.UdpSessionData;
import com.dnk.smart.redis.data.rw.WebServerData;
import com.dnk.smart.tcp.cache.dict.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * redis server data key
 */
@RequiredArgsConstructor
@Getter
public enum DataKeyEnum {
    TCP_SESSION("TS", TcpSessionData.class),
    UDP_V2_SESSION("UV2S", UdpSessionData.class),
    TCP_SERVER_REGISTER("TSR", TcpServerData.class),
    COMMAND_V2_QUEUE("CV2Q", Command.class),

    /**
     * 以下在webServer中处理
     */
    @Deprecated
    COMMAND_V1_QUEUE("CV1Q", Command.class),
    @Deprecated
    WEB_SERVER_REGISTER("WSR", WebServerData.class),
    @Deprecated
    UDP_V1_SESSION("UV1S", UdpSessionData.class);

    private final String key;
    private final Class<?> clazz;
}
