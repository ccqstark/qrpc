package com.ccqstark.qrpc.core.remoting.transport.netty.codec;

import com.ccqstark.qrpc.common.enums.CompressTypeEnum;
import com.ccqstark.qrpc.common.enums.SerializationTypeEnum;
import com.ccqstark.qrpc.common.extension.ExtensionLoader;
import com.ccqstark.qrpc.core.compress.Compress;
import com.ccqstark.qrpc.core.remoting.constants.RpcConstants;
import com.ccqstark.qrpc.core.remoting.dto.RpcMessage;
import com.ccqstark.qrpc.core.remoting.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ccqstark
 * @description 解码器
 * @date 2022/6/14 01:04
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
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) throws Exception {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            // 预留4个字节用于写消息总长度
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            out.writeByte(rpcMessage.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeByte(ATOMIC_INTEGER.getAndIncrement());

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // 如果消息类型不是心跳的话，fullLength总长度 = head长度 + body长度
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE &&
                    messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // 序列化
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name [{}]", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // 压缩字节
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            // 把fullLength总长度写入之前留出来的位置
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error", e);
        }
    }
}
