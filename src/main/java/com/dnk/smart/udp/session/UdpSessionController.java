package com.dnk.smart.udp.session;

import io.netty.channel.Channel;
import lombok.NonNull;

import java.net.InetSocketAddress;

public interface UdpSessionController {

    Channel channel();

    UdpInfo info(@NonNull String sn);

    void receive(@NonNull UdpInfo info);

    void response(@NonNull InetSocketAddress target);

    void awake(@NonNull InetSocketAddress target);

}
