package com.dnk.smart.tcp.cache.dict;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(chain = true)
public final class Command {
    private final String id;
    private final String terminalId;
    private final String content;
    @Setter
    private long runtime;
}