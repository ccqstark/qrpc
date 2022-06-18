package com.ccqstark.qrpc.core.remoting.transport.netty.codec;

import com.ccqstark.qrpc.common.enums.CompressTypeEnum;
import com.ccqstark.qrpc.common.enums.SerializationTypeEnum;
import com.ccqstark.qrpc.common.extension.ExtensionLoader;
import com.ccqstark.qrpc.core.compress.Compress;
import com.ccqstark.qrpc.core.remoting.constants.RpcConstants;
import com.ccqstark.qrpc.core.remoting.dto.RpcMessage;
import com.ccqstark.qrpc.core.remoting.dto.RpcRequest;
import com.ccqstark.qrpc.core.remoting.dto.RpcResponse;
import com.ccqstark.qrpc.core.remoting.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author ccqstark
 * @description 编码器
 * @date 2022/6/6 13:38
 * <p>
 * 编码协议/格式:
 * 0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 * |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔数）   1B version（版本）     4B full length（消息长度）    1B messageType（消息类型）
 * 1B codec（序列化类型）   1B compress（压缩类型）  4B  requestId（请求的Id）
 * body（object类型数据）
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset: 魔数长度4B，版本长度1B，后面才是总长度fullLength，所以此处为5
        // lengthFieldLength：总长度字段的数据长4B，所以这里为4
        // lengthAdjustment：从开头到读完fullLength，已经过去了9个字节，剩余数据长度为(fullLength-9)，所以这里为-9
        // initialBytesToStrip：魔数和版本号要我们手动check，所以不从开头删除任何字节，此处为0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * 构造器
     *
     * @param maxFrameLength      最大帧长度，如果数据超过这个长度就会被丢弃
     * @param lengthFieldOffset   表示从数据包开头到fullLength，需要跳过多少字节的数据
     * @param lengthFieldLength   总长度字段的数据的长度
     * @param lengthAdjustment    要添加到 length 字段值中的补偿值
     * @param initialBytesToStrip 从开头起始要删除的字节数(如果想接收整个数据体，那此时该值为0；如果只想接收body，那值为header的长度)
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 调用父类的decode把这一数据帧完整抽出来，利用的就是构造器里面的那些参数
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.HEADER_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    /**
     * 对ByteBuf中的数据进行解析，得到RpcMessage对象
     *
     * @param in ByteBuf
     * @return RpcMessage
     */
    private Object decodeFrame(ByteBuf in) {
        // 必须按编码协议的顺序读/处理
        // 首先检查魔数和版本号是否正确
        checkMagicNumber(in);
        checkVersion(in);
        // 再按顺序读取总长度、消息类型、序列化类型、压缩类型、请求id
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        // 构造RpcMessage对象
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        // 心跳类型
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            // 解压
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            bs = compress.decompress(bs);
            // 反序列化
            String codecName = SerializationTypeEnum.getName(codecType);
            log.info("codec name: [{}]", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            // 根据body是请求还是响应，反序列化得到body内容
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest requestBodyData = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(requestBodyData);
            } else {
                RpcResponse responseBodyData = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(responseBodyData);
            }
        }
        return rpcMessage;
    }

    /**
     * 检查魔数
     */
    private void checkMagicNumber(ByteBuf in) {
        // 读取头四个bit，也就是魔数
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

    /**
     * 检查版本号
     */
    private void checkVersion(ByteBuf in) {
        // 读取版本并与当前版本进行比较
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

}
