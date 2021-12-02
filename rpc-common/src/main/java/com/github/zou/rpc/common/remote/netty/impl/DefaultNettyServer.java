package com.github.zou.rpc.common.remote.netty.impl;


import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import com.github.zou.rpc.common.remote.netty.NettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * netty 网络服务端
 * @author zou
 * @since 1.0.0
 */
public class DefaultNettyServer extends AbstractNettyServer {

    /**
     * 日志信息
     * @since 1.0.0
     */
    private static final Log LOG = LogFactory.getLog(DefaultNettyServer.class);

    /**
     * channel 信息
     * @since 1.0.0
     */
    private ChannelFuture channelFuture;

    /**
     * boss 线程池
     * @since 1.0.0
     */
    private EventLoopGroup bossGroup;

    /**
     * worker 线程池
     * @since 1.0.0
     */
    private EventLoopGroup workerGroup;

    private DefaultNettyServer(int port, ChannelHandler channelHandler) {
        super(port, channelHandler);
    }

    public static NettyServer newInstance(int port, ChannelHandler channelHandler) {
        return new DefaultNettyServer(port, channelHandler);
    }

    @Override
    public void run() {
        LOG.info("[Netty Server] start with port: {} and channelHandler: {} ",
                port, channelHandler.getClass().getSimpleName());

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(workerGroup, bossGroup)
                    .channel(NioServerSocketChannel.class)
                    // 打印日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(channelHandler)
                    // 这个参数影响的是还没有被accept 取出的连接
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 这个参数只是过一段时间内客户端没有响应，服务端会发送一个 ack 包，以判断客户端是否还活着。
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，开始接收进来的链接
            channelFuture = serverBootstrap.bind(port).syncUninterruptibly();
            LOG.info("[Netty Server] 启动完成，监听【" + port + "】端口");

        } catch (Exception e) {
            LOG.error("[Netty Server] 服务启动异常", e);
            throw new RpcRuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            LOG.info("[Netty Server] 开始关闭");
            channelFuture.channel().close();
            LOG.info("[Netty Server] 完成关闭");
        } catch (Exception e) {
            LOG.error("[Netty Server] 关闭服务异常", e);
            throw new RpcRuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
