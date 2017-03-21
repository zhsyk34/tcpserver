package com.dnk.smart.tcp.cache.dict;

import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public final class Command {
    private String id;
    @NonNull
    private String terminalId;
    @NonNull
    private String content;
    private long runtime;
}