package com.dnk.smart.tcp.message.publish;

/**
 * 与webServer通讯
 */
public interface WebMessageProcessor {

    /**
     * web请求指令处理结果响应
     */
    void publishWebCommandResult(String webServerId, boolean result);
}
