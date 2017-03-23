package com.dnk.smart.dict.redis.channel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class WebCommandRequestData {
    @NonNull
    private String serverId;
    @NonNull
    private String sn;
}
