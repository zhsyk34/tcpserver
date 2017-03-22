package com.dnk.smart.redis.data.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class GatewayUdpPortApplyData {
    private String ip;
    private String sn;
    private int apply;
}