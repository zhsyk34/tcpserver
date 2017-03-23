package com.dnk.smart.tcp.state;

import com.dnk.smart.dict.tcp.LoginInfo;
import io.netty.channel.Channel;
import lombok.NonNull;

public interface StateController {

    void accept(@NonNull Channel channel);

    void onAccept(@NonNull Channel channel);

    void request(@NonNull Channel channel, @NonNull LoginInfo info);

    void onRequest(@NonNull Channel channel);

    void verify(@NonNull Channel channel, @NonNull String answer);

    void onVerify(@NonNull Channel channel, boolean result);

    void await(@NonNull Channel channel);

    void onAwait(@NonNull Channel channel);

    void success(@NonNull Channel channel, int allocated);

    void onSuccess(@NonNull Channel channel);

    void close(@NonNull Channel channel);

    void onClose(@NonNull Channel channel);

}
