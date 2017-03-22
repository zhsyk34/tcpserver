package com.dnk.smart.tcp.message.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TcpSessionData {
    private final String sn;
    private String ip;
    private int port;
    private int apply;//申请的udp端口 50003
    private int allocated;//分配的udp端口 50004
    private long updateTime;
}
