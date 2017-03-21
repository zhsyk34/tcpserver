package com.dnk.smart.tcp.cache;

import com.dnk.smart.tcp.cache.dict.Command;
import com.dnk.smart.tcp.cache.dict.LoginInfo;
import com.dnk.smart.tcp.cache.dict.State;
import com.dnk.smart.tcp.cache.dict.Verifier;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service
public final class SimpleChannelCacheAccessor implements ChannelCacheAccessor {
    private static final AttributeKey<LoginInfo> LOGIN_INFO_ATTRIBUTE_KEY = AttributeKey.newInstance(LoginInfo.class.getSimpleName());
    private static final AttributeKey<State> STATE_ATTRIBUTE_KEY = AttributeKey.newInstance(State.class.getSimpleName());
    private static final AttributeKey<Verifier> VERIFIER_ATTRIBUTE_KEY = AttributeKey.newInstance(Verifier.class.getSimpleName());
    private static final AttributeKey<Command> COMMAND_ATTRIBUTE_KEY = AttributeKey.newInstance(Command.class.getSimpleName());

    @Override
    public String id(@NonNull Channel channel) {
        return channel.id().asShortText();
    }

    @Override
    public String ip(@NonNull Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public int port(@NonNull Channel channel) {
        return ((InetSocketAddress) channel.remoteAddress()).getPort();
    }

    @Override
    public LoginInfo info(@NonNull Channel channel) {
        return channel.attr(LOGIN_INFO_ATTRIBUTE_KEY).get();
    }

    @Override
    public void info(@NonNull Channel channel, @NonNull LoginInfo loginInfo) {
        channel.attr(LOGIN_INFO_ATTRIBUTE_KEY).set(loginInfo);
    }

    @Override
    public State state(@NonNull Channel channel) {
        return channel.attr(STATE_ATTRIBUTE_KEY).get();
    }

    @Override
    public void state(@NonNull Channel channel, @NonNull State state) {
        channel.attr(STATE_ATTRIBUTE_KEY).set(state);
    }

    @Override
    public Verifier verifier(@NonNull Channel channel) {
        return channel.attr(VERIFIER_ATTRIBUTE_KEY).get();
    }

    @Override
    public void verifier(@NonNull Channel channel, @NonNull Verifier verifier) {
        channel.attr(VERIFIER_ATTRIBUTE_KEY).set(verifier);
    }

    @Override
    public Command command(@NonNull Channel channel) {
        return channel.attr(COMMAND_ATTRIBUTE_KEY).get();
    }

    @Override
    public void command(@NonNull Channel channel, @NonNull Command command) {
        channel.attr(COMMAND_ATTRIBUTE_KEY).set(command);
    }

}
