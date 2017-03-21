package com.dnk.smart.tcp;

import com.alibaba.fastjson.JSONObject;
import com.dnk.smart.dict.Action;
import com.dnk.smart.dict.Key;
import com.dnk.smart.dict.Result;
import com.dnk.smart.kit.JsonKit;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.tcp.cache.Command;
import com.dnk.smart.tcp.cache.DataAccessor;
import com.dnk.smart.tcp.cache.LoginInfo;
import com.dnk.smart.tcp.message.cache.RedisMessageAccessor;
import com.dnk.smart.tcp.message.direct.ClientMessageProcessor;
import com.dnk.smart.tcp.message.publish.ChannelMessageProcessor;
import com.dnk.smart.tcp.task.CommandProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 处理除登录以外的请求,登录验证已在此前的 {@link TcpLoginHandler} 中处理
 * <p>
 * 1.app 指令请求保存至redisServer同时广播通知其它tcpServer
 * 2.gateway
 * 2-1.心跳:ClientMessageProcessor直接回复
 * 2-2.推送信息:发布至DbMessageProcessor
 * 2-3.版本请求:发布至DbMessageProcessor并订阅回复
 * 2-4.指令响应:根据Command类型广播 ==> 并尝试继续执行任务
 */
@Component
final class TcpServerHandler extends ChannelInboundHandlerAdapter {
    @Resource
    private DataAccessor dataAccessor;
    @Resource
    private ClientMessageProcessor clientMessageProcessor;
    @Resource
    private RedisMessageAccessor redisMessageAccessor;
    @Resource
    private ChannelMessageProcessor channelMessageProcessor;
    @Resource
    private CommandProcessor commandProcessor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof String)) {
            return;
        }
        Channel channel = ctx.channel();
        String command = (String) msg;

        JSONObject json = JsonKit.map(command);
        Action action = Action.from(json.getString(Key.ACTION.getName()));
        Result result = Result.from(json.getString(Key.RESULT.getName()));

        LoginInfo info = dataAccessor.info(channel);
        String sn = info.getSn();

        switch (info.getDevice()) {
            case APP:
                if (action != null) {
                    Log.logger(Factory.TCP_RECEIVE, "app请求[" + command + "]");
                    redisMessageAccessor.shareAppCommand(dataAccessor.id(channel), command);
                    Log.logger(Factory.TCP_RECEIVE, "广播app请求");
                    channelMessageProcessor.publishAppCommandRequest(sn);
                }
                break;
            case GATEWAY:
                //1.心跳
                if (action == Action.HEART_BEAT) {
                    Log.logger(Factory.TCP_RECEIVE, "网关[" + sn + "] 发送心跳");
                    clientMessageProcessor.responseHeartbeat(channel);
                    return;
                }

                //2.推送
                if (action != null && action.getType() == 3) {
                    Log.logger(Factory.TCP_RECEIVE, "网关[" + sn + "] 推送数据...");
                    channelMessageProcessor.publishPushMessage(sn, command);//append sn
                    return;
                }

                //3.版本
                if (action == Action.GET_VERSION) {
                    channelMessageProcessor.publishForGatewayVersion(sn);
                    return;
                }

                //4.响应请求
                if (result != null) {
                    Log.logger(Factory.TCP_EVENT, "网关[" + sn + "] 接收到指令处理结果");

                    Command current = dataAccessor.command(channel);
                    if (current == null) {
                        System.err.println("wrong!!!");
                        //won't happen TODO
                        return;
                    }
                    if (StringUtils.hasText(current.getId())) {
                        channelMessageProcessor.publishAppCommandResult(current.getTerminalId(), command);
                    } else {
                        channelMessageProcessor.publishWebCommandResult(current.getTerminalId(), result == Result.OK);
                    }

                    //TODO
                    commandProcessor.rest(channel);
                    commandProcessor.startup(channel);
                }
                break;
            default:
                break;
        }
    }
}
