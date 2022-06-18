package com.ccqstark.qrpc.core.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author ccqstark
 * @description 常量
 * @date 2022/6/7 13:44
 */
public class RpcConstants {

    /**
     * 魔数 (默认为utf-8编码)
     * utf-8编码: 英文字母转byte后只占1个字节长度，中文占3个字节长度
     * gbk编码: 无论英文，中文都占2个字节长度
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'q', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 版本信息
     */
    public static final byte VERSION = 1;

    /**
     * 消息头长度
     */
    public static final byte HEADER_LENGTH = 16;

    /**
     * 普通请求、响应类型
     */
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;

    /**
     * 心跳请求、响应类型
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    /**
     * 心跳信号
     */
    public static final String PING = "ping";
    public static final String PONG = "pong";

    /**
     * 协议头长度
     */
    public static final int HEAD_LENGTH = 16;

    /**
     * 最大帧长度
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
