package com.dnk.smart.tcp.cache.dict;

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
}
