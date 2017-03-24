package com.dnk.smart.dict.tcp;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.config.Config;
import com.dnk.smart.dict.Key;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Builder
public final class LoginInfo {
    private String sn;
    private Device device;
    private int apply;
    private int allocated;
    private long happen;

    public static LoginInfo from(@NonNull JSONObject json) {
        String sn = json.getString(Key.SN.getName());
        Device device = Device.from(json.getIntValue(Key.TYPE.getName()));
        int apply = json.getIntValue(Key.APPLY.getName());
        return LoginInfo.builder().sn(sn).device(device).apply(apply).build();
    }

    public LoginInfo update(@NonNull LoginInfo other) {
        if (StringUtils.hasText(other.getSn())) {
            this.setSn(other.getSn());
        }
        if (other.getDevice() != null) {
            this.setDevice(other.getDevice());
        }
        if (other.getApply() >= Config.TCP_ALLOT_MIN_UDP_PORT) {
            this.setApply(other.getApply());
        }
        return this;
    }
}