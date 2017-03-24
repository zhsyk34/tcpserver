package com.dnk.smart.log;

import io.netty.handler.logging.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

/**
 * 日志记录器仓库
 */
@AllArgsConstructor
@Getter
public enum Factory {

    TCP_RECEIVE(Target.TCP, Category.RECEIVE),
    TCP_SEND(Target.TCP, Category.SEND),
    TCP_EVENT(Target.TCP, Category.EVENT),
    TCP_ERROR(Target.TCP, Category.ERROR),

    UDP_RECEIVE(Target.UDP, Category.RECEIVE),
    UDP_SEND(Target.UDP, Category.SEND),
    UDP_EVENT(Target.UDP, Category.EVENT),
    UDP_ERROR(Target.UDP, Category.ERROR),

    PROJECT_COMMON(Target.PROJECT, Category.COMMON);

    private static final EnumMap<Factory, Logger> LOGGER_REGISTER = new EnumMap<>(Factory.class);

    static {
        for (Factory factory : values()) {
            String name = factory.getTarget() + "-" + factory.getCategory();
            LOGGER_REGISTER.put(factory, LoggerFactory.getLogger(name));
        }
    }

    private final Target target;
    private final Category category;

    static Logger logger(@NonNull Factory factory) {
        return LOGGER_REGISTER.get(factory);
    }

    /**
     * 提供默认日志级别
     */
    static LogLevel level(@NonNull Category category) {
        switch (category) {
            case RECEIVE:
                return LogLevel.INFO;
            case SEND:
                return LogLevel.INFO;
            case EVENT:
                return LogLevel.WARN;
            case ERROR:
                return LogLevel.ERROR;
            case COMMON:
                return LogLevel.INFO;
            default:
                return LogLevel.DEBUG;
        }
    }

    static Factory from(Target target, Category category) {
        for (Factory factory : values()) {
            if (factory.getTarget() == target && factory.getCategory() == category) {
                return factory;
            }
        }
        return null;
    }
}
