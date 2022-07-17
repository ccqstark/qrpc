package io.github.ccqstark.qrpc.core.compress;

import io.github.ccqstark.qrpc.common.extension.SPI;

/**
 * @author ccqstark
 * @description 压缩接口
 * @date 2022/6/16 01:36
 */
@SPI
public interface Compress {

    /**
     * 压缩
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压缩
     */
    byte[] decompress(byte[] bytes);

}
