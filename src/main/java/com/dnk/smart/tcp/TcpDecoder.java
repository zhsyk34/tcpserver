package com.dnk.smart.tcp;

import com.dnk.smart.config.Config;
import com.dnk.smart.config.Protocol;
import com.dnk.smart.log.Factory;
import com.dnk.smart.log.Log;
import com.dnk.smart.util.ByteKit;
import com.dnk.smart.util.DESKit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.util.CharsetUtil;

import java.util.List;

import static com.dnk.smart.util.CodecKit.validateVerify;

/**
 * 解码TCP服务器接收到的数据
 * 由于业务处理场景基于一问一答方式,故此每次解码默认只有一个包
 * 具体解码时简单处理了粘包的问题(主要发生在网关主动推送信息时)//TODO
 * <p>
 * 当包头数据非法时直接丢弃数据,否则等待正确的包尾数据直至缓冲区大于指定值
 * 当包头包尾均正确时开始解析:
 * 根据长度定位到帧尾,如果数据正确且缓冲 buffer.size > packet.size 则可能存在粘包==>截取解析后的数据后递归调用该方法继续解码剩余部分
 * <p>
 * 该解析方法不能正确处理半包情况(基于业务情境忽略此种情况)
 */
final class TcpDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int size = in.readableBytes();

        //length
        if (size < Protocol.MSG_MIN_LENGTH) {
            Log.logger(Factory.TCP_RECEIVE, "等待数据中...数据至少应有[" + Protocol.MSG_MIN_LENGTH + "]位");
            return;
        }
        if (size > Config.TCP_BUFFER_SIZE) {
            Log.logger(Factory.TCP_RECEIVE, "缓冲数据已达[" + size + "]位,超过最大限制[" + Config.TCP_BUFFER_SIZE + "],丢弃本次数据");
            in.clear();
            return;
        }

        in.markReaderIndex();

        //header
        if (in.readByte() != Protocol.HEADERS.get(0) || in.readByte() != Protocol.HEADERS.get(1)) {
            in.clear();
            Log.logger(Factory.TCP_RECEIVE, "包头数据错误,丢弃本次数据");
            return;
        }

        //direct to check footer
        if (in.getByte(size - 2) != Protocol.FOOTERS.get(0) || in.getByte(size - 1) != Protocol.FOOTERS.get(1)) {
            in.resetReaderIndex();
            Log.logger(Factory.TCP_RECEIVE, "包尾数据错误,尝试继续等待...");
            return;
        }

        //length
        int length = ByteKit.byteArrayToInt(new byte[]{in.readByte(), in.readByte()});
        int actual = length - Protocol.LENGTH_BYTES - Protocol.VERIFY_BYTES;
        Log.logger(Factory.TCP_RECEIVE, LogLevel.TRACE, "校验长度:[" + length + "], 指令长度应为:[" + actual + "]");
        if (actual < Protocol.MIN_DATA_BYTES || actual > size - Protocol.REDUNDANT_BYTES) {
            in.clear();
            Log.logger(Factory.TCP_RECEIVE, "[长度校验数据]校验错误,丢弃本次数据");
            return;
        }

        //skip dict and verify code to check footer again
        in.markReaderIndex();
        in.skipBytes(actual + Protocol.VERIFY_BYTES);

        if (in.readByte() != Protocol.FOOTERS.get(0) || in.readByte() != Protocol.FOOTERS.get(1)) {
            in.clear();
            Log.logger(Factory.TCP_RECEIVE, "通过[长度校验]获取包尾数据错误,丢弃本次数据");
            return;
        }

        //read dict
        in.resetReaderIndex();

        byte[] data = new byte[actual];
        ByteBuf dataBuf = in.readBytes(actual);
        dataBuf.getBytes(0, data).release();

        //code
        if (!validateVerify(data, new byte[]{in.readByte(), in.readByte()})) {
            in.clear();
            Log.logger(Factory.TCP_RECEIVE, "[校验码]错误,丢弃本次数据");
            return;
        }

        //skip footer
        in.skipBytes(Protocol.FOOTERS.size());

        String command = new String(DESKit.decrypt(data), CharsetUtil.UTF_8);
        out.add(command);

        //recursion
        if (in.readableBytes() > 0) {
            Log.logger(Factory.TCP_RECEIVE, LogLevel.TRACE, "解析剩余部分");
            decode(ctx, in, out);
        }
    }

}
