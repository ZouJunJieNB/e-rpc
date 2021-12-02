package com.github.zou.rpc.register.api.config;

/**
 * 注册配置接口信息
 * @author zou
 * @since 1.0.0
 */
public interface RegisterConfig {

    /**
     * 服务端口号
     * @param port 端口号
     * @return this
     * @since 1.0.0
     */
    RegisterConfig port(final int port);

    /**
     * 启动服务
     * @return this
     * @since 1.0.0
     */
    RegisterConfig start();

}
