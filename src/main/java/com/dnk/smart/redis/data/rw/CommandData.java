package com.dnk.smart.redis.data.rw;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommandData {
    private String terminalId;//
    private String command;//{action:....}
    private String messageId; //webServerId + uuid();
}

