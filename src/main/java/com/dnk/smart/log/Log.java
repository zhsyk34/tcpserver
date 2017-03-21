package com.dnk.smart.log;

import io.netty.handler.logging.LogLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

import static com.dnk.smart.config.Config.LOGGER_CAPACITY;

/**
 * 日志管理
 */
public class Log {

    /**
     * TODO
     * 待写入日志队列
     */
    private final static LinkedBlockingQueue<Content> LOGGER_QUEUE = new LinkedBlockingQueue<>(LOGGER_CAPACITY);

    public static void logger(Factory factory, LogLevel level, String msg, Throwable throwable) {
        level = level == null ? Factory.level(factory.getCategory()) : level;
        Logger logger = Factory.logger(factory);
        Content con = new Content(logger, level, msg, throwable);
        write(con);
//		LOGGER_QUEUE.offer(con);
    }

    public static void logger(Factory factory, LogLevel level, String msg) {
        logger(factory, level, msg, null);
    }

    public static void logger(Factory factory, String msg, Throwable throwable) {
        logger(factory, null, msg, throwable);
    }

    public static void logger(Factory factory, String msg) {
        logger(factory, null, msg, null);
    }

    private static void execute() {
        if (LOGGER_QUEUE.size() > 0) {
            try {
                Content content = LOGGER_QUEUE.take();
                write(content);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger logger = Factory.logger(Factory.PROJECT_COMMON);
                logger.error("logger queue is full.", e);
            }
        }
    }

    private static void write(Content content) {
        if (content.getThrowable() == null) {
            write(content.getLogger(), content.getLevel(), content.getMsg());
        } else {
            write(content.getLogger(), content.getLevel(), content.getMsg(), content.getThrowable());
        }
    }

    private static void write(Logger logger, LogLevel level, String msg) {
        switch (level) {
            case ERROR:
                logger.error(msg);
                break;
            case WARN:
                logger.warn(msg);
                break;
            case INFO:
                logger.info(msg);
                break;
            case DEBUG:
                logger.debug(msg);
                break;
            case TRACE:
                logger.trace(msg);
                break;
            default:
                break;
        }
    }

    private static void write(Logger logger, LogLevel level, String msg, Throwable throwable) {
        switch (level) {
            case ERROR:
                logger.error(msg, throwable);
                break;
            case WARN:
                logger.warn(msg, throwable);
                break;
            case INFO:
                logger.info(msg, throwable);
                break;
            case DEBUG:
                logger.debug(msg, throwable);
                break;
            case TRACE:
                logger.trace(msg, throwable);
                break;
            default:
                break;
        }
    }

    /**
     * 日志内容
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Content {
        @NonNull
        private final Logger logger;
        @NonNull
        private final LogLevel level;
        @NonNull
        private final String msg;
        private final Throwable throwable;
    }
}
