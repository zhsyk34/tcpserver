package com.dnk.smart.dict.udp;

import com.dnk.smart.config.Config;
import com.dnk.smart.dict.redis.cache.UdpSessionData;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
@Getter
public final class UdpInfo {
    @NonNull
    private final String sn;
    @NonNull
    private final String ip;
    private final int port;
    private final String version;
    private final long happen;

    public static UdpInfo from(String sn, InetSocketAddress address, String version) {
        String ip = address.getAddress().getHostAddress();
        int port = address.getPort();
        return new UdpInfo(sn, ip, port, version, System.currentTimeMillis());
    }

    public static UdpInfo from(@NonNull UdpSessionData sessionData) {
        return new UdpInfo(sessionData.getSn(), sessionData.getIp(), sessionData.getPort(), sessionData.getVersion(), sessionData.getHappen());
    }

    public static UdpSessionData to(@NonNull UdpInfo info) {
        return new UdpSessionData(Config.TCP_SERVER_ID, info.getSn(), info.getIp(), info.getPort(), info.getVersion(), info.getHappen());
    }
}
