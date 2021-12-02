package com.github.zou.rpc.client.proxy.impl;


import com.github.houbb.heaven.support.handler.IHandler;
import com.github.houbb.heaven.util.id.impl.Ids;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.client.proxy.RemoteInvokeContext;
import com.github.zou.rpc.client.proxy.RemoteInvokeService;
import com.github.zou.rpc.client.proxy.ServiceContext;
import com.github.zou.rpc.client.support.calltype.CallTypeStrategy;
import com.github.zou.rpc.client.support.calltype.impl.CallTypeStrategyFactory;
import com.github.zou.rpc.client.support.fail.FailStrategy;
import com.github.zou.rpc.client.support.fail.impl.FailStrategyFactory;
import com.github.zou.rpc.client.support.filter.RpcFilter;
import com.github.zou.rpc.common.support.invoke.InvokeManager;
import com.github.zou.rpc.client.support.register.ClientRegisterManager;
import com.github.zou.rpc.common.rpc.domain.RpcChannelFuture;
import com.github.zou.rpc.common.rpc.domain.RpcRequest;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;
import com.github.zou.rpc.common.support.balance.ILoadBalance;
import com.github.zou.rpc.common.support.balance.impl.LoadBalanceContext;
import com.github.zou.rpc.common.support.balance.server.IServer;
import io.netty.channel.Channel;

import java.util.List;

/**
 * 远程调用实现
 * @author zou
 * @since 1.0.0
 */
public class RemoteInvokeServiceImpl implements RemoteInvokeService {

    private static final Log LOG = LogFactory.getLog(RemoteInvokeServiceImpl.class);

    @Override
    public Object remoteInvoke(RemoteInvokeContext context) {
        final RpcRequest rpcRequest = context.request();
        final ServiceContext proxyContext = context.serviceProxyContext();
        final RpcFilter rpcFilter = proxyContext.rpcFilter();


        // 设置唯一标识
        final String seqId = Ids.uuid32();
        rpcRequest.seqId(seqId);

        // 构建 filter 相关信息,结合 pipeline 进行整合
        rpcFilter.filter(context);

        // 负载均衡
        // 这里使用 load-balance 进行选择 channel 写入。
        final Channel channel = getLoadBalanceChannel(proxyContext);
        LOG.info("[Client] start call channel id: {}", channel.id().asLongText());

        // 对于信息的写入，实际上有着严格的要求。
        // writeAndFlush 实际是一个异步的操作，直接使用 sync() 可以看到异常信息。
        // 支持的必须是 ByteBuf
        channel.writeAndFlush(rpcRequest).syncUninterruptibly();
        LOG.info("[Client] start call remote with request: {}", rpcRequest);
        final InvokeManager invokeManager = proxyContext.invokeManager();
        invokeManager.addRequest(seqId, proxyContext.timeout());

        // 获取结果
        CallTypeStrategy callTypeStrategy = CallTypeStrategyFactory.callTypeStrategy(proxyContext.callType());
        RpcResponse rpcResponse = callTypeStrategy.result(proxyContext, rpcRequest);
        invokeManager.removeReqAndResp(seqId);

        // 获取调用结果
        context.rpcResponse(rpcResponse);
        FailStrategy failStrategy = FailStrategyFactory.failStrategy(proxyContext.failType());
        return failStrategy.fail(context);
    }

    /**
     * 获取负载均衡的 channel
     * @param serviceContext 服务上下文
     * @return 结果
     */
    private Channel getLoadBalanceChannel(final ServiceContext serviceContext) {
        final ClientRegisterManager clientRegisterManager = serviceContext.clientRegisterManager();
        final String serviceId = serviceContext.serviceId();
        List<RpcChannelFuture> channelFutures = clientRegisterManager.queryServerChannelFutures(serviceId);

        final ILoadBalance loadBalance = serviceContext.loadBalance();
        List<IServer> servers = CollectionUtil.toList(channelFutures, new IHandler<RpcChannelFuture, IServer>() {
            @Override
            public IServer handle(RpcChannelFuture rpcChannelFuture) {
                return rpcChannelFuture;
            }
        });
        LoadBalanceContext context = LoadBalanceContext.newInstance()
                .servers(servers);

        IServer server = loadBalance.select(context);
        LOG.info("负载均衡获取地址信息：{}", server.url());
        RpcChannelFuture future = (RpcChannelFuture) server;
        return future.channelFuture().channel();
    }

}
