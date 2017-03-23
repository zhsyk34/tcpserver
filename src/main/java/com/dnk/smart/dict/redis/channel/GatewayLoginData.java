package com.dnk.smart.dict.redis.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class GatewayLoginData {
    private String sn;
    private String serverId;
}
