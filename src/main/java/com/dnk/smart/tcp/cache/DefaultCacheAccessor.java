package com.dnk.smart.tcp.cache;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.tcp.cache.dict.Command;
import com.dnk.smart.tcp.cache.dict.TcpInfo;
import com.dnk.smart.tcp.cache.dict.UdpInfo;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dnk.smart.tcp.cache.dict.RedisKey.*;

@Service
public class DefaultCacheAccessor extends SimpleChannelCacheAccessor implements CacheAccessor {

    @Resource
    private RedisAccessor redisAccessor;

    @Override
    public void registerGatewayTcpSessionInfo(@NonNull TcpInfo info) {
        redisAccessor.put(TCP_SESSION, info.getSn(), JSON.toJSONString(info));
    }

    @Override
    public void unregisterGatewayTcpSessionInfo(@NonNull String sn) {
        redisAccessor.remove(TCP_SESSION, sn);
    }

    @Override
    public void reportUdpSessionInfo(@NonNull UdpInfo info) {
        redisAccessor.put(UDP_V2_SESSION, info.getSn(), JSON.toJSONString(info));
    }

    @Override
    public void reportServerStatus(@NonNull String serverId) {
        redisAccessor.put(TCP_SERVER_REGISTER, serverId, Long.toString(System.currentTimeMillis()));
    }

    @Override
    public void shareAppCommand(@NonNull String appId, @NonNull String command) {
        redisAccessor.push(COMMAND_V2_QUEUE, command);
    }

    @Override
    public Command getFirstCommand(@NonNull String sn) {
        return JSON.parseObject(redisAccessor.pop(COMMAND_V2_QUEUE), Command.class);
    }

    @Override
    public List<Command> getAllCommand(@NonNull String sn) {
        List<String> list = redisAccessor.popAll(COMMAND_V2_QUEUE);
        return Optional.ofNullable(list).map(Collection::stream).orElse(Stream.empty()).map(data -> JSON.parseObject(data, Command.class)).collect(Collectors.toList());
    }

}
