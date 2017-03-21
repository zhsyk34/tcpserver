package com.dnk.smart.udp.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
@Getter
public final class UdpInfo {
    private final String sn;
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
