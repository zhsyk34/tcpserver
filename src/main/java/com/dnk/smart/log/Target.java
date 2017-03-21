package com.dnk.smart.log;

/**
 * 日志对象
 */
public enum Target {
    TCP, UDP, PROJECT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
