package com.dnk.smart.tcp.cache;

import com.dnk.smart.tcp.cache.dict.*;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultDataAccessor implements DataAccessor {
    @Override
    public void registerGatewayTcpSessionInfo(@NonNull TcpInfo info) {

    }

    @Override
    public void unregisterGatewayTcpSessionInfo(@NonNull String sn) {

    }

    @Override
    public void reportUdpSessionInfo(@NonNull UdpInfo info) {

    }

    @Override
    public void reportServerStatus(@NonNull String serverId) {

    }

    @Override
    public void shareAppCommand(@NonNull String appId, @NonNull String command) {

    }

    @Override
    public Command getFirstCommand(@NonNull String sn) {
        return null;
    }

    @Override
    public List<Command> getAllCommand(@NonNull String sn) {
        return null;
    }

    @Override
    public String id(@NonNull Channel channel) {
        return null;
    }

    @Override
    public String ip(@NonNull Channel channel) {
        return null;
    }

    @Override
    public int port(@NonNull Channel channel) {
        return 0;
    }

    @Override
    public LoginInfo info(@NonNull Channel channel) {
        return null;
    }

    @Override
    public void info(@NonNull Channel channel, @NonNull LoginInfo loginInfo) {

    }

    @Override
    public State state(@NonNull Channel channel) {
        return null;
    }

    @Override
    public void state(@NonNull Channel channel, @NonNull State state) {

    }

    @Override
    public Verifier verifier(@NonNull Channel channel) {
        return null;
    }

    @Override
    public void verifier(@NonNull Channel channel, Verifier verifier) {

    }

    @Override
    public Command command(@NonNull Channel channel) {
        return null;
    }

    @Override
    public void command(@NonNull Channel channel, @NonNull Command command) {

    }
}
