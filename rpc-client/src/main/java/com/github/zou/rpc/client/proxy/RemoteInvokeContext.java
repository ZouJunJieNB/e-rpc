package com.github.zou.rpc.client.proxy;

import com.github.zou.rpc.client.proxy.impl.DefaultRemoteInvokeContext;
import com.github.zou.rpc.common.rpc.domain.RpcChannelFuture;
import com.github.zou.rpc.common.rpc.domain.RpcRequest;
import com.github.zou.rpc.common.rpc.domain.RpcResponse;

/**
 * 远程调用上下文
 *
 * 核心目的：
 * （1）用于定义 filter 相关信息
 * （2）用于 load-balance 相关信息处理
 *
 * @author zou
 * @since 1.0.0
 */
public interface RemoteInvokeContext<T> {

    /**
     * 请求信息
     * @return 请求信息
     */
    RpcRequest request();

    /**
     * 服务代理上下文信息
     * @return 服务代理信息
     */
    ServiceContext<T> serviceProxyContext();

    /**
     * 设置 channel future
     * （1）可以通过 load balance
     * （2）其他各种方式
     * @param channelFuture 消息
     * @return this
     */
    RemoteInvokeContext<T> channelFuture(final RpcChannelFuture channelFuture);

    /**
     * 请求响应结果
     * @return 请求响应结果
     */
    RpcResponse rpcResponse();

    /**
     * 请求响应结果
     * @param rpcResponse 响应结果
     * @return 请求响应结果
     */
    DefaultRemoteInvokeContext<T> rpcResponse(final RpcResponse rpcResponse);

    /**
     * 获取重试次数
     * @return 重试次数
     */
    int retryTimes();

    /**
     * 设置重试次数
     * @param retryTimes 设置重试次数
     * @return this
     */
    DefaultRemoteInvokeContext<T> retryTimes(final int retryTimes);

    /**
     * 在整个调用生命周期中唯一的标识号
     * （1）重试也不会改变
     * （2）只在第一次调用的时候进行设置。
     * @return 订单号
     */
    String traceId();

    /**
     * 远程调用服务信息
     * @return 远程调用服务信息
     */
    RemoteInvokeService remoteInvokeService();

}
