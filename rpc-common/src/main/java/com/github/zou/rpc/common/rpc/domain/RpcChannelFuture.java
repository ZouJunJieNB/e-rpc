package com.github.zou.rpc.common.rpc.domain;


import com.github.zou.rpc.common.api.Destroyable;
import com.github.zou.rpc.common.config.component.RpcAddress;
import com.github.zou.rpc.common.support.balance.server.IServer;
import io.netty.channel.ChannelFuture;


/**
 * <p> rpc channel future 接口</p>
 *
 * @author zou
 * @since 1.0.0
 */
public interface RpcChannelFuture extends IServer {

    /**
     * channel future 信息
     * @return ChannelFuture
     * @since 0.0.9
     */
    ChannelFuture channelFuture();

    /**
     * 对应的地址信息
     * @return 地址信息
     */
    RpcAddress address();

    /**
     * 可销毁的对象
     * @return 可销毁的信息
     */
    Destroyable destroyable();

}
