package com.dnk.smart.redis.data.rw;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TcpServerData {
    private String serverId;
    private long updateTime;
}
