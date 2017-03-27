package com.dnk.smart.dict.redis.cache;

import com.dnk.smart.config.Config;
import com.dnk.smart.dict.udp.UdpInfo;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UdpSessionData {
    private String serverId;

    private String sn;
    private String ip;
    private int port;
    private String version;
    private long happen;

    public static UdpSessionData from(@NonNull UdpInfo info) {
        return new UdpSessionData(Config.TCP_SERVER_ID, info.getSn(), info.getIp(), info.getPort(), info.getVersion(), info.getHappen());
    }
}
