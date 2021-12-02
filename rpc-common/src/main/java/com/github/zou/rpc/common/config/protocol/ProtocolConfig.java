package com.github.zou.rpc.common.config.protocol;

/**
 * 协议配置信息
 * @author zou
 * @since 1.0.0
 */
public interface ProtocolConfig {

    /**
     * 名称
     * RPC
     * HTTP
     * HTTPS
     * @since 0.0.6
     * @return 协议名称
     */
    String name();

    /**
     * 协议端口号
     * @return 端口号
     */
    int port();

}
