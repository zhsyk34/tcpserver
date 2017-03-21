package com.dnk.smart.tcp.cache;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.dnk.smart.tcp.cache.Device.GATEWAY;

/**
 * only gateway
 */
@RequiredArgsConstructor
@Getter
public final class TcpInfo {
    private final String sn;
    private final int apply;
    private final int allocated;
    private final long happen;

    public static TcpInfo from(@NonNull LoginInfo info) {
        if (info.getDevice() != GATEWAY) {
            return null;
        }
        return new TcpInfo(info.getSn(), info.getApply(), info.getAllocated(), info.getHappen());
    }
}
