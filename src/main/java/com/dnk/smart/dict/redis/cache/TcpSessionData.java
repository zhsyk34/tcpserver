package com.dnk.smart.dict.redis.cache;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class TcpSessionData {
    @NonNull
    private String serverId;//服务器编号
    @NonNull
    private String sn;
    @NonNull
    private String ip;
    private int port;
    private int apply;//申请的udp端口 50003
    private int allocated;//分配的udp端口 50004
    private long happen;
}
