package com.dnk.smart.tcp.task;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.dict.Key;
import com.dnk.smart.dict.Result;
import com.dnk.smart.tcp.cache.DataAccessor;
import com.dnk.smart.tcp.cache.dict.Command;
import com.dnk.smart.tcp.cache.dict.LoginInfo;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.dnk.smart.tcp.cache.dict.Device.GATEWAY;

@Service
public class DefaultCommandProcessor implements CommandProcessor {
    @Resource
    private DataAccessor dataAccessor;
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
        if (dataAccessor.command(channel) != null) {
            return;
        }

        synchronized (this) {
            if (dataAccessor.command(channel) == null) {
                LoginInfo info = dataAccessor.info(channel);
                Command command = dataAccessor.getFirstCommand(info.getSn());
                dataAccessor.command(channel, command);
            }
        }
    }

    @Override
    public void execute(@NonNull Channel channel) {
        Command command = dataAccessor.command(channel);
        if (command == null) {
            return;
        }

        synchronized (this) {
            if (dataAccessor.command(channel) != null) {
                command.setRuntime(System.currentTimeMillis());
                channel.writeAndFlush(command.getContent());
            }
        }
    }

    @Override
    public void startup(@NonNull String sn) {
        Channel channel = sessionRegistry.getGatewayChannel(sn);
        if (channel == null) {
            return;
        }
        this.startup(channel);
    }

    @Override
    public void startup(@NonNull Channel channel) {
        synchronized (this) {
            this.prepare(channel);
            this.execute(channel);
        }
    }

    @Override
    public void rest(@NonNull Channel channel) {
        dataAccessor.command(channel, null);
    }

    @Override
    public void clear(@NonNull Channel channel) {
        if (!this.check(channel)) {
            return;
        }
        String sn = dataAccessor.info(channel).getSn();
        List<Command> commands = dataAccessor.getAllCommand(sn);

        if (!CollectionUtils.isEmpty(commands)) {
            JSONObject json = new JSONObject();
            json.put(Key.RESULT.getName(), Result.NO.getName());

            commands.forEach((command) -> {
                String terminalId = command.getTerminalId();
                if (command.getId() == null) {
                    channelMessageProcessor.publishAppCommandResult(terminalId, json.toString());
                } else {
                    channelMessageProcessor.publishWebCommandResult(terminalId, false);
                }
            });
        }

        dataAccessor.command(channel, null);
    }

    private boolean check(@NonNull Channel channel) {
        LoginInfo info = dataAccessor.info(channel);
        return info != null && info.getDevice() == GATEWAY && StringUtils.hasText(info.getSn());
    }
}
