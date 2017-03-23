package com.dnk.smart.dict.tcp;

import com.dnk.smart.util.CodecKit;
import com.dnk.smart.util.RandomUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Verifier {
    @NonNull
    private final String question;
    @NonNull
    private final String answer;

    public static Verifier generator() {
        int group = RandomUtils.randomInteger(0, 49);
        int offset = RandomUtils.randomInteger(0, 9);
        return new Verifier(CodecKit.loginKey(group, offset), CodecKit.loginVerify(group, offset));
    }
}
