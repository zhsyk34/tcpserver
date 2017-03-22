package com.dnk.smart.tcp.message.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UdpSessionData {
    private final String sn;
    private String ip;
    private int port;
    private String version;
    private long updateTime;
}
