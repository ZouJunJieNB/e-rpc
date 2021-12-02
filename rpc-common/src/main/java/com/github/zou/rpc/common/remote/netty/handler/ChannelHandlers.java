package com.github.zou.rpc.common.remote.netty.handler;


import com.github.houbb.heaven.util.guava.Guavas;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.zou.rpc.common.config.component.RpcAddress;
import com.github.zou.rpc.common.remote.netty.impl.DefaultNettyClient;
import com.github.zou.rpc.common.rpc.domain.RpcChannelFuture;
import com.github.zou.rpc.common.rpc.domain.impl.DefaultRpcChannelFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * channel 工具类
 * @author zou
 * @since 1.0.0
 */
public final class ChannelHandlers {

    private ChannelHandlers(){}

    /**
     * 包含默认 object 编码解码的 handler
     * @param channelHandlers 用户自定义 handler
     * @return channel handler
     * @since 1.0.0
     */
    public static ChannelHandler objectCodecHandler(final ChannelHandler ... channelHandlers) {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                        // 解码 bytes=>resp
                        .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                        // request=>bytes
                        .addLast(new ObjectEncoder())
                        .addLast(channelHandlers);
            }
        };
    }

    /**
     * 包含默认 object 编码解码的 + 日志 handler
     * @param channelHandlers 用户自定义 handler
     * @return channel handler
     * @since 1.0.0
     */
    public static ChannelHandler objectCodecLogHandler(final ChannelHandler ... channelHandlers) {
        final ChannelHandler objectCodecHandler = objectCodecHandler(channelHandlers);
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LoggingHandler(LogLevel.INFO))
                        .addLast(objectCodecHandler)
                        .addLast(channelHandlers);
            }
        };
    }

    /**
     * 获取处理后的channel future 列表信息
     * （1）权重
     * （2）client 链接信息
     * （3）地址信息
     * @param rpcAddressList 地址信息列表
     * @param handlerFactory 构建工厂
     * @return 信息列表
     * @since 0.0.9
     */
    public static List<RpcChannelFuture> channelFutureList(final List<RpcAddress> rpcAddressList, final ChannelHandlerFactory handlerFactory) {
        List<RpcChannelFuture> resultList = Guavas.newArrayList();

        if(CollectionUtil.isNotEmpty(rpcAddressList)) {
            for(RpcAddress rpcAddress : rpcAddressList) {
                final ChannelHandler channelHandler = handlerFactory.handler();

                // 循环中每次都需要一个新的 handler
                DefaultRpcChannelFuture future = DefaultRpcChannelFuture.newInstance();
                DefaultNettyClient nettyClient = DefaultNettyClient.newInstance(rpcAddress.address(), rpcAddress.port(), channelHandler);
                ChannelFuture channelFuture = nettyClient.call();

                future.channelFuture(channelFuture).address(rpcAddress)
                        .weight(rpcAddress.weight()).destroyable(nettyClient);
                resultList.add(future);
            }
        }

        return resultList;
    }

    /**
     * 获取处理后的channel future 信息
     * （1）权重
     * （2）client 链接信息
     * （3）地址信息
     * @param rpcAddress 地址信息
     * @param handlerFactory 构建工厂
     * @return 信息列表
     * @since 0.1.6
     */
    public static RpcChannelFuture channelFuture(final RpcAddress rpcAddress, final ChannelHandlerFactory handlerFactory) {
        final ChannelHandler channelHandler = handlerFactory.handler();

        // 循环中每次都需要一个新的 handler
        DefaultRpcChannelFuture future = DefaultRpcChannelFuture.newInstance();
        DefaultNettyClient nettyClient = DefaultNettyClient.newInstance(rpcAddress.address(), rpcAddress.port(), channelHandler);
        ChannelFuture channelFuture = nettyClient.call();

        future.channelFuture(channelFuture)
                .address(rpcAddress)
                .weight(rpcAddress.weight())
                .destroyable(nettyClient);

        return future;
    }

}
