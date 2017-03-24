package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.dict.redis.RedisChannel;
import com.dnk.smart.dict.redis.channel.WebCommandRequestData;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.tcp.command.CommandProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.dict.redis.RedisChannel.WEB_COMMAND_REQUEST;

@Service
public class WebMessageListener extends AbstractRedisListener {
    @Resource
    private CommandProcessor processor;

    WebMessageListener() {
        super(WEB_COMMAND_REQUEST);
    }

    @Override
    void handleMessage(RedisChannel redisChannel, byte[] content) {
        switch (redisChannel) {
            case WEB_COMMAND_REQUEST:
                WebCommandRequestData data = JSON.parseObject(content, WEB_COMMAND_REQUEST.getClazz());
                String serverId = data.getServerId();

                if (TCP_SERVER_ID.equals(serverId)) {
                    String sn = data.getSn();
                    Log.logger(Factory.TCP_EVENT, "尝试执行任务...");
                    processor.startup(sn);
                }
                break;
            default:
                break;
        }
    }
}
