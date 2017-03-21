package com.dnk.smart.log;

/**
 * 日志分类
 */
public enum Category {
    RECEIVE,
    SEND,
    EVENT,
    ERROR,
    COMMON;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
