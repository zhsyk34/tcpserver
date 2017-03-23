package com.dnk.smart.udp.session;

import com.dnk.smart.dict.udp.UdpInfo;
import io.netty.channel.Channel;
import lombok.NonNull;

import java.net.InetSocketAddress;

public interface UdpSessionController {

    Channel channel();

    void receive(@NonNull UdpInfo info);

    void response(@NonNull InetSocketAddress target);

    UdpInfo info(@NonNull String sn);

    void awake(@NonNull InetSocketAddress target);

    /**
     * @return 是否能发出唤醒信息(不代表结果)
     */
    boolean awake(@NonNull String sn);

}
