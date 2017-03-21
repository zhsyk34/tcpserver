package com.dnk.smart.tcp.cache.dict;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * only gateway
 */
@RequiredArgsConstructor
@Getter
public final class TcpInfo {
    @NonNull
    private final String sn;
    private final int apply;
    private final int allocated;
    private final long happen;

    public static TcpInfo from(@NonNull LoginInfo info) {
        if (info.getDevice() != Device.GATEWAY) {
            return null;
        }
        return new TcpInfo(info.getSn(), info.getApply(), info.getAllocated(), info.getHappen());
    }
}
