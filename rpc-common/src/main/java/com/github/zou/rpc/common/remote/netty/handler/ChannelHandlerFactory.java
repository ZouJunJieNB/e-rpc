package com.github.zou.rpc.common.remote.netty.handler;

import io.netty.channel.ChannelHandler;

/**
 * <p> 用户构建 channel handler </p>
 *
 * @author zou
 * @since 0.0.9
 */
public interface ChannelHandlerFactory {

    /**
     * 构建 handler 信息
     * @return ChannelHandler
     * @since 0.0.9
     */
    ChannelHandler handler();

}
