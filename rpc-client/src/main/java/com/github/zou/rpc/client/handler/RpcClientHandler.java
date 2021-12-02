
package com.github.zou.rpc.client.handler;


import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.support.invoke.InvokeManager;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p> 客户端处理类 </p>
 *
 * @author zou
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler {

    private static final Log log = LogFactory.getLog(RpcClientHandler.class);

    /**
     * 调用服务管理类
     *
     * @since 0.0.6
     */
    private final InvokeManager invokeManager;

    public RpcClientHandler(InvokeManager invokeManager) {
        this.invokeManager = invokeManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse)msg;
        invokeManager.addResponse(rpcResponse.seqId(), rpcResponse);
        log.info("[Client] server response is :{}", rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[Rpc Client] meet ex ", cause);
        ctx.close();
    }

}
