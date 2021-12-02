package com.github.zou.rpc.common.remote.netty.impl;


import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.exception.RpcRuntimeException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty 网络客户端
 * @author zou
 * @since 1.0.0
 */
public class DefaultNettyClient extends AbstractNettyClient<ChannelFuture> {

    /**
     * 工作线程池
     * @since 1.0.0
     */
    private EventLoopGroup workerGroup;

    /**
     * channel 信息
     * @since 1.0.0
     */
    private ChannelFuture channelFuture;

    private DefaultNettyClient(String ip, int port, ChannelHandler channelHandler) {
        super(ip, port, channelHandler);
    }

    /**
     * 创建新的对象实例
     * @param ip 地址
     * @param port 端口
     * @param channelHandler 实现类
     * @return 对象实例
     * @since 1.0.0
     */
    public static DefaultNettyClient newInstance(String ip, int port,
                                                 ChannelHandler channelHandler) {
        return new DefaultNettyClient(ip, port, channelHandler);
    }

    /**
     * 日志信息
     * @since 1.0.0
     */
    private static final Log LOG = LogFactory.getLog(DefaultNettyClient.class);

    @Override
    public ChannelFuture call() {
        // 启动服务端
        LOG.info("[Netty Client] 开始启动客户端");

        workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            channelFuture = bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(channelHandler)
                    .connect(ip, port)
                    .syncUninterruptibly();
            LOG.info("[Netty Client] 启动客户端完成，监听地址 {}:{}", ip, port);
        } catch (Exception e) {
            LOG.error("[Netty Client] 端启动遇到异常", e);
            throw new RpcRuntimeException(e);
        }
        // 不要关闭线程池！！！
        return channelFuture;
    }

    /**
     * 关闭客户端
     *
     * 即closeFuture()是开启了一个channel的监听器，负责监听channel是否关闭的状态，
     * 如果未来监听到channel关闭了，子线程才会释放，syncUninterruptibly()让主线程同步等待子线程结果。
     *
     * <pre>
     *     channelFuture.channel().closeFuture().syncUninterruptibly();
     * </pre>
     *
     */
    @Override
    public void destroy() {
        try {
            LOG.info("[Netty Client] start close future.");
            channelFuture.channel().close();
            LOG.info("[Netty Client] 关闭完成");
        } catch (Exception e) {
            LOG.error("[Netty Client] 关闭服务异常", e);
            throw new RpcRuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            LOG.info("[Netty Client] 线程池关闭完成");
        }
    }

}
