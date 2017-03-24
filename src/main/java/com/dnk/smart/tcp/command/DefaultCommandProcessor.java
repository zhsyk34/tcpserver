package com.dnk.smart.tcp.command;

import com.dnk.smart.dict.redis.cache.Command;
import com.dnk.smart.tcp.cache.CacheAccessor;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.dnk.smart.dict.tcp.State.CLOSED;
import static com.dnk.smart.dict.tcp.State.SUCCESS;

@Service
public class DefaultCommandProcessor implements CommandProcessor {
    @Resource
    private CacheAccessor cacheAccessor;
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;

    @Override
    public void prepare(@NonNull Channel channel) {
        if (cacheAccessor.state(channel) == SUCCESS && cacheAccessor.command(channel) == null) {
            cacheAccessor.command(channel, cacheAccessor.getFirstCommand(cacheAccessor.info(channel).getSn()));
        }
    }

    @Override
    public void execute(@NonNull Channel channel) {
        Command command = cacheAccessor.command(channel);
        if (command != null) {
            command.setRuntime(System.currentTimeMillis());
            channel.writeAndFlush(command.getContent());
        }
    }

    @Override
    public void startup(@NonNull String sn) {
        Channel channel = sessionRegistry.getGatewayChannel(sn);
        if (channel != null) {
            this.startup(channel);
        }
    }

    @Override
    public void startup(@NonNull Channel channel) {
        this.prepare(channel);
        this.execute(channel);
    }

    @Override
    public void reset(@NonNull Channel channel) {
        cacheAccessor.command(channel, null);
    }

    @Override
    public void restart(@NonNull Channel channel) {
        if (cacheAccessor.command(channel) != null) {
            this.reset(channel);
            this.startup(channel);
        }
    }

    @Override
    public void clean(@NonNull String sn) {
        Optional.ofNullable(cacheAccessor.getAllCommand(sn)).ifPresent(list -> list.forEach(channelMessageProcessor::publishCommandFail));
    }

    @Override
    public void clean(@NonNull Channel channel) {
        if (cacheAccessor.state(channel) != CLOSED) {
            return;
        }

        List<Command> commands = Optional.ofNullable(cacheAccessor.getAllCommand(cacheAccessor.info(channel).getSn())).orElse(new ArrayList<>());
        Optional.ofNullable(cacheAccessor.command(channel)).ifPresent(commands::add);

        commands.forEach(channelMessageProcessor::publishCommandFail);

        cacheAccessor.command(channel, null);
    }

}
