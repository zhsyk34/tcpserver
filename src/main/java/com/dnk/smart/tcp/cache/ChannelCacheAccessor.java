package com.dnk.smart.tcp.cache;

import com.dnk.smart.dict.redis.cache.Command;
import com.dnk.smart.dict.tcp.LoginInfo;
import com.dnk.smart.dict.tcp.State;
import com.dnk.smart.dict.tcp.Verifier;
import io.netty.channel.Channel;
import lombok.NonNull;

public interface ChannelCacheAccessor {

    /*---------------------------------------base data by getGatewayChannel self---------------------------------------*/

    String id(@NonNull Channel channel);

    String ip(@NonNull Channel channel);

    int port(@NonNull Channel channel);

    /*---------------------------------------cache data for login---------------------------------------*/

    LoginInfo info(@NonNull Channel channel);

    void info(@NonNull Channel channel, @NonNull LoginInfo loginInfo);

    State state(@NonNull Channel channel);

    void state(@NonNull Channel channel, @NonNull State state);

    Verifier verifier(@NonNull Channel channel);

    void verifier(@NonNull Channel channel, Verifier verifier);

    /*---------------------------------------cache data for gateway running---------------------------------------*/

    Command command(@NonNull Channel channel);

    void command(@NonNull Channel channel, @NonNull Command command);

}
