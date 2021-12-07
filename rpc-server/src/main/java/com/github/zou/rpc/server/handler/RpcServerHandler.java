package com.github.zou.rpc.server.handler;


import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.constant.enums.CallTypeEnum;
import com.github.zou.rpc.common.rpc.domain.RpcRequest;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import com.github.zou.rpc.common.rpc.domain.impl.DefaultRpcResponse;
import com.github.zou.rpc.common.rpc.domain.impl.RpcResponseFactory;
import com.github.zou.rpc.common.support.invoke.InvokeManager;
import com.github.zou.rpc.common.support.status.enums.StatusEnum;
import com.github.zou.rpc.common.support.status.service.StatusManager;
import com.github.zou.rpc.server.service.impl.DefaultServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zou
 * @since 0.0.1
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler {

    private static final Log log = LogFactory.getLog(RpcServerHandler.class);

    /**
     * 调用管理类
     * @since 0.1.3
     */
    private final InvokeManager invokeManager;

    /**
     * 状态管理类
     * @since 0.1.3
     */
    private final StatusManager statusManager;

    public RpcServerHandler(InvokeManager invokeManager, StatusManager statusManager) {
        this.invokeManager = invokeManager;
        this.statusManager = statusManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String id = ctx.channel().id().asLongText();
        log.info("[Server] channel {} connected " + id);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        final String id = ctx.channel().id().asLongText();
        log.info("[Server] channel read start: {}", id);

        // 1. 接受客户端请求
        RpcRequest rpcRequest = (RpcRequest)msg;
        final String seqId = rpcRequest.seqId();
        try {
            log.info("[Server] receive seqId: {} request: {}", seqId, rpcRequest);
            // 2. 设置请求信息和超时时间
            invokeManager.addRequest(rpcRequest.seqId(), rpcRequest.timeout());

            // 3. 回写到 client 端
            //3.1 获取结果
            RpcResponse rpcResponse = this.handleRpcRequest(rpcRequest);
            //3.2 回写结果
            final CallTypeEnum callType = rpcRequest.callType();
            if(CallTypeEnum.SYNC.equals(callType)) {
                ctx.writeAndFlush(rpcResponse);
            } else {
                log.info("[Server] seqId: {} callType: {} ignore write back.", seqId, callType);
            }
            log.info("[Server] seqId: {} response {}", seqId, rpcResponse);
        } finally {
            // 3.3 移除对应的信息，便于优雅关闭
            invokeManager.removeReqAndResp(seqId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[Server] meet ex: ", cause);
        ctx.close();
    }

    /**
     * 处理请求信息
     * （1）这里为了简单考虑，暂时不采用异步执行的方式，直接同步执行
     * （2）缺点就是超时检测的数据会被忽略。
     *
     * @param rpcRequest 请求信息
     * @return 结果信息
     * @since 0.0.6
     */
    private RpcResponse handleRpcRequest(final RpcRequest rpcRequest) {
        //1. 判断是否为 shutdown 状态
        // 状态判断
        final int statusCode = statusManager.status();
        if(StatusEnum.ENABLE.code() != statusCode) {
            log.error("[Server] current status is: {} , not enable to handle request", statusCode);
            return RpcResponseFactory.shutdown();
        }



        //3. 异步执行调用，这样才能进行检测
        DefaultRpcResponse rpcResponse = new DefaultRpcResponse();
        rpcResponse.seqId(rpcRequest.seqId());

        try {
            // 获取对应的 service 实现类
            // rpcRequest=>invocationRequest
            // 执行 invoke
            Object result = DefaultServiceFactory.getInstance()
                    .invoke(rpcRequest.serviceId(),
                            rpcRequest.methodName(),
                            rpcRequest.paramTypeNames(),
                            rpcRequest.paramValues());
            rpcResponse.result(result);
        } catch (Exception e) {
            rpcResponse.error(e);
            log.error("[Server] execute meet ex for request: {}", rpcRequest, e);
        }

        // 构建结果值
        return rpcResponse;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开链接" + ctx.channel().localAddress().toString());
    }


}
