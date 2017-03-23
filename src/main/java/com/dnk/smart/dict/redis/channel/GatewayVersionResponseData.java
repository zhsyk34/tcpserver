package com.dnk.smart.dict.redis.channel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class GatewayVersionResponseData {
    @NonNull
    private String sn;
    @NonNull
    private String result;
}
