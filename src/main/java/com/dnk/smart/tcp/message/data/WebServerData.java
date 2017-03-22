package com.dnk.smart.tcp.message.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WebServerData {
    private String serverId;
    private long updateTime;
}
