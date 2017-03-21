package com.dnk.smart.tcp.message.subscribe;

import com.alibaba.fastjson.JSON;
import com.dnk.smart.redis.data.dict.ChannelNameEnum;
import com.dnk.smart.redis.data.pub.WebCommandRequestData;
import com.dnk.smart.tcp.task.CommandProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.dnk.smart.config.Config.TCP_SERVER_ID;
import static com.dnk.smart.redis.data.dict.ChannelNameEnum.WEB_COMMAND_REQUEST;

@Component
public class WebMessageListener extends AbstractRedisListener {
    @Resource
    private CommandProcessor processor;

    WebMessageListener() {
        super(WEB_COMMAND_REQUEST);
    }

    @Override
    void handleMessage(ChannelNameEnum channelNameEnum, byte[] content) {
        switch (channelNameEnum) {
            case WEB_COMMAND_REQUEST:
                WebCommandRequestData data = JSON.parseObject(content, WEB_COMMAND_REQUEST.getClazz());
                String serverId = data.getServerId();
                String sn = data.getSn();

                if (TCP_SERVER_ID.equals(serverId)) {
                    processor.startup(sn);
                }
                break;
            default:
                break;
        }
    }
}
