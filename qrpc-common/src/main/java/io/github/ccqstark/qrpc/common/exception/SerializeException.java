package io.github.ccqstark.qrpc.common.exception;

/**
 * @author ccqstark
 * @description 自定义序列化异常
 * @date 2022/6/5 17:40
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
