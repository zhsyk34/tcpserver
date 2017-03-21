package com.dnk.smart.tcp.task;

import com.dnk.smart.tcp.cache.Command;
import com.dnk.smart.tcp.cache.DataAccessor;
import com.dnk.smart.tcp.cache.LoginInfo;
import com.dnk.smart.tcp.message.cache.RedisMessageAccessor;
import com.dnk.smart.tcp.session.SessionRegistry;
import io.netty.channel.Channel;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.dnk.smart.tcp.cache.Device.GATEWAY;

@Service
public class DefaultCommandProcessor implements CommandProcessor {
    @Resource
    private DataAccessor dataAccessor;
    @Resource
    private RedisMessageAccessor redisMessageAccessor;
    @Resource
    private SessionRegistry sessionRegistry;

    @Override
    public void prepare(@NonNull String sn) {
        Channel channel = sessionRegistry.channel(sn);
        if (channel == null) {
            return;
        }
        //synchronized
        if (dataAccessor.underway(channel)) {
            return;
        }

        Command command = redisMessageAccessor.getFirstCommand(sn);

        dataAccessor.underway(channel, command != null);
        dataAccessor.command(channel, command);
    }

    @Override
    public void prepare(@NonNull Channel channel) {
        LoginInfo info = dataAccessor.info(channel);
        if (info == null || info.getDevice() != GATEWAY) {
            return;
        }
        if (dataAccessor.underway(channel)) {
            return;
        }

        Command command = redisMessageAccessor.getFirstCommand(info.getSn());

        dataAccessor.underway(channel, command != null);
        dataAccessor.command(channel, command);
    }

    @Override
    public void execute(@NonNull Channel channel) {
        Command command = dataAccessor.command(channel);
        if (command != null) {
            command.setRuntime(System.currentTimeMillis());
            channel.writeAndFlush(command.getContent());
        } else {
            this.rest(channel);
        }
    }

    @Override
    public void startup(@NonNull Channel channel) {
        this.prepare(channel);
        this.execute(channel);
    }

    @Override
    public void rest(@NonNull Channel channel) {
        dataAccessor.command(channel, null);
        dataAccessor.underway(channel, false);
    }
}
