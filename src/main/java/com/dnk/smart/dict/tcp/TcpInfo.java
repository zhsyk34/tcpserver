package com.dnk.smart.dict.tcp;

import lombok.*;

/**
 * only gateway
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public final class TcpInfo {
    @NonNull
    private String sn;
    private int apply;
    private int allocated;
    private long happen;

    public static TcpInfo from(@NonNull LoginInfo info) {
        if (info.getDevice() != Device.GATEWAY) {
            return null;
        }
        return new TcpInfo(info.getSn(), info.getApply(), info.getAllocated(), info.getHappen());
    }
}
