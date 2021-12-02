package com.github.zou.rpc.common.remote.netty.impl;


import com.github.zou.rpc.common.remote.netty.NettyClient;
import io.netty.channel.ChannelHandler;

/**
 * netty 网络服务端
 * @author zou
 * @since 1.0.0
 * @param <V> 泛型
 */
public abstract class AbstractNettyClient<V> implements NettyClient<V> {

    /**
     * ip 信息
     * @since 1.0.0
     */
    protected String ip;

    /**
     * 端口信息
     * @since 1.0.0
     */
    protected int port;

    /**
     * channel handler
     * @since 1.0.0
     */
    protected ChannelHandler channelHandler;

    public AbstractNettyClient(String ip, int port, ChannelHandler channelHandler) {
        this.ip = ip;
        this.port = port;
        this.channelHandler = channelHandler;
    }
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

}
