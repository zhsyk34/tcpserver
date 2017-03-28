package com.dnk.smart.dict.redis.cache;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter
@Setter
@Accessors(chain = true)
public final class Command {
    private String id;
    @NonNull
    private String terminalId;
    @NonNull
    private String content;
    private long runtime;
}