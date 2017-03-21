package com.dnk.smart.tcp.cache;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public final class SimpleDataAccessor implements DataAccessor {
    private static final AttributeKey<LoginInfo> LOGIN_INFO_ATTRIBUTE_KEY = AttributeKey.newInstance(LoginInfo.class.getSimpleName());
    private static final AttributeKey<Verifier> VERIFIER_ATTRIBUTE_KEY = AttributeKey.newInstance(Verifier.class.getSimpleName());
    private static final AttributeKey<State> STATE_ATTRIBUTE_KEY = AttributeKey.newInstance(State.class.getSimpleName());
    private static final AttributeKey<Command> COMMAND_ATTRIBUTE_KEY = AttributeKey.newInstance(Command.class.getSimpleName());
    private static final AttributeKey<AtomicBoolean> UNDERWAY_ATTRIBUTE_KEY = AttributeKey.newInstance(AtomicBoolean.class.getSimpleName());

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

    @Override
    public boolean underway(@NonNull Channel channel) {
        AtomicBoolean underway = channel.attr(UNDERWAY_ATTRIBUTE_KEY).get();
        return underway != null && underway.get();
    }

    @Override
    public void underway(@NonNull Channel channel, @NonNull boolean underway) {
        AtomicBoolean exists = channel.attr(UNDERWAY_ATTRIBUTE_KEY).get();
        if (exists == null) {
            channel.attr(UNDERWAY_ATTRIBUTE_KEY).set(new AtomicBoolean(underway));
        } else {
            exists.set(underway);
        }
    }

}
