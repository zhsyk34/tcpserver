package com.dnk.smart.tcp.command;

import com.dnk.smart.dict.redis.cache.Command;
import com.dnk.smart.dict.tcp.State;
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

import static com.dnk.smart.dict.tcp.State.SUCCESS;

//TODO:synchronized
@Service
public class DefaultCommandProcessor implements CommandProcessor {
    @Resource
    private CacheAccessor cacheAccessor;
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;

    @Override
    public void prepare(@NonNull String sn) {
        Channel channel = sessionRegistry.getGatewayChannel(sn);
        if (channel == null) {
            return;
        }
        this.prepare(channel);
    }

    @Override
    public void prepare(@NonNull Channel channel) {
        if (!this.check(channel)) {
            return;
        }

        if (cacheAccessor.command(channel) == null) {
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
    public void rest(@NonNull Channel channel) {
        cacheAccessor.command(channel, null);
    }

    @Override
    public void clean(@NonNull String sn) {
        Optional.ofNullable(cacheAccessor.getAllCommand(sn)).ifPresent(list -> list.forEach(command -> {
            String terminalId = command.getTerminalId();
            if (command.getId() == null) {
                channelMessageProcessor.publishAppCommandFail(terminalId);
            } else {
                channelMessageProcessor.publishWebCommandResult(terminalId, false);
            }
        }));
    }

    @Override
    public void clean(@NonNull Channel channel) {
        if (!this.check(channel)) {
            return;
        }

        Command current = cacheAccessor.command(channel);
        String sn = cacheAccessor.info(channel).getSn();

        List<Command> commands = Optional.ofNullable(cacheAccessor.getAllCommand(sn)).orElse(new ArrayList<>());
        Optional.ofNullable(current).ifPresent(commands::add);

        commands.forEach(command -> {
            String terminalId = command.getTerminalId();
            if (command.getId() == null) {
                channelMessageProcessor.publishAppCommandFail(terminalId);//app
            } else {
                channelMessageProcessor.publishWebCommandResult(terminalId, false);
            }
        });

        cacheAccessor.command(channel, null);
    }

    private boolean check(@NonNull Channel channel) {
        State state = cacheAccessor.state(channel);
        return state != null && state.isAfter(SUCCESS);
    }
}
