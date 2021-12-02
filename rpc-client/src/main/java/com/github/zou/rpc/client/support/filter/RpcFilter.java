package com.github.zou.rpc.client.support.filter;

import com.github.zou.rpc.client.proxy.RemoteInvokeContext;

/**
 * 核心目的：
 * （1）用于定义 filter 相关信息
 * （2）用于 load-balance 相关信息处理
 * （3）后期的路由-分区 都可以视为这样的一个抽象实现而已。
 *
 * 插件式实现：
 * （1）远程调用也认为是一次 filter，上下文中构建 filter-chain
 * （2）filter-chain 可以使用 {@link Pipeline} 管理
 *
 *
 * 后期拓展：
 * （1）类似于 aop，用户可以自行定义 interceptor 拦截器
 * @author zou
 * @since 1.0.0
 */
public interface RpcFilter {


    /**
     * filter 处理
     * @param context 上下文
     * @since 0.0.9
     */
    void filter(final RemoteInvokeContext context);

}
