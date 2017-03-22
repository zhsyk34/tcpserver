package com.dnk.smart.tcp.message.subscribe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
public class DefaultRedisListener {

    @Resource
    private RedisMessageListenerContainer container;

    private List<RedisListener> listeners;

    @Autowired
    public void setListeners(List<RedisListener> listeners) {
        this.listeners = listeners;
    }

    @PostConstruct
    public void monitor() {
        if (!CollectionUtils.isEmpty(listeners)) {
            listeners.forEach(listener -> container.addMessageListener(listener.listener(), listener.topics()));
        }
    }
}
