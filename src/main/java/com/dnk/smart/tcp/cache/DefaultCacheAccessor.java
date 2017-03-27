package com.dnk.smart.tcp.cache;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.dict.redis.cache.Command;
import com.dnk.smart.dict.redis.cache.TcpSessionData;
import com.dnk.smart.dict.redis.cache.UdpSessionData;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dnk.smart.dict.redis.RedisKey.*;

/**
 * TODO:parse object == null?
 */
@Service
public class DefaultCacheAccessor extends SimpleChannelCacheAccessor implements CacheAccessor {

    @Resource
    private RedisAccessor redisAccessor;

    @Override
    public void registerGatewayTcpSessionInfo(@NonNull TcpSessionData sessionData) {
        redisAccessor.put(TCP_SESSION, sessionData.getSn(), JSON.toJSONString(sessionData));
    }

    @Override
    public TcpSessionData getGatewayTcpSessionInfo(@NonNull String sn) {
        return JSON.parseObject(redisAccessor.get(TCP_SESSION, sn), TcpSessionData.class);
    }

    @Override
    public void unregisterGatewayTcpSessionInfo(@NonNull String sn) {
        redisAccessor.remove(TCP_SESSION, sn);
    }

    @Override
    public void reportUdpSessionInfo(@NonNull UdpSessionData sessionData) {
        redisAccessor.put(UDP_V2_SESSION, sessionData.getSn(), JSON.toJSONString(sessionData));
    }

    @Override
    public UdpSessionData getUdpSessionInfo(@NonNull String sn) {
        return JSON.parseObject(redisAccessor.get(UDP_V2_SESSION, sn), UdpSessionData.class);
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
        return Optional.ofNullable(list).map(Collection::stream).orElse(Stream.empty()).map(data -> JSON.parseObject(data, Command.class)).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
