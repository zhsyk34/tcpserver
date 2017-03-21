package com.dnk.smart.tcp.session;

import io.netty.channel.Channel;
import lombok.NonNull;

public interface CapableSessionRegistry {

    Channel channel(@NonNull String sn);

    /**
     * awake gateway(only) to login
     */
    boolean awake(@NonNull String sn);

//    boolean close(@NonNull Device device, @NonNull String key);

    /**
     * close gateway channel when it login in another server
     */
    boolean close(@NonNull String sn);
}
