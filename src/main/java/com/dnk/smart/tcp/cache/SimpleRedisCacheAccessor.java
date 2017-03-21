package com.dnk.smart.tcp.cache;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.kit.RedisUtils;
import com.dnk.smart.tcp.cache.dict.Command;
import com.dnk.smart.tcp.cache.dict.TcpInfo;
import com.dnk.smart.tcp.cache.dict.UdpInfo;
import lombok.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnk.smart.redis.data.dict.DataKeyEnum.*;

@Repository
public class SimpleRedisCacheAccessor implements RedisCacheAccessor {
    @Resource
    private RedisUtils redisUtils;

    @Override
    public void registerGatewayTcpSessionInfo(@NonNull TcpInfo info) {
        redisUtils.put(TCP_SESSION, info.getSn(), JSON.toJSONString(info));
    }

    @Override
    public void unregisterGatewayTcpSessionInfo(@NonNull String sn) {
        redisUtils.remove(TCP_SESSION.getKey(), sn);
    }

    @Override
    public void reportUdpSessionInfo(@NonNull UdpInfo info) {
        redisUtils.put(UDP_V2_SESSION, info.getSn(), JSON.toJSONString(info));
    }

    @Override
    public void reportServerStatus(@NonNull String serverId) {
        redisUtils.put(TCP_SERVER_REGISTER, serverId, Long.toString(System.currentTimeMillis()));
    }

    @Override
    public void shareAppCommand(@NonNull String appId, @NonNull String command) {
        redisUtils.push(COMMAND_V2_QUEUE, command);
    }

    @Override
    public Command getFirstCommand(@NonNull String sn) {
        String data = redisUtils.pop(COMMAND_V2_QUEUE);
        return JSON.parseObject(data, Command.class);
    }

    @Override
    public List<Command> getAllCommand(@NonNull String sn) {
        List<String> list = redisUtils.popAll(COMMAND_V2_QUEUE);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(data -> JSON.parseObject(data, Command.class)).collect(Collectors.toList());
    }
}
