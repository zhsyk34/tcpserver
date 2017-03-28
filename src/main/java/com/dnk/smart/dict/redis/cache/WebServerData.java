package com.dnk.smart.dict.redis.cache;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
public class WebServerData {
    @NonNull
    private String serverId;
    private long happen;
}
