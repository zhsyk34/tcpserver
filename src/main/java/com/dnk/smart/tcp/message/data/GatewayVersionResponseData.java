package com.dnk.smart.tcp.message.data;

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
