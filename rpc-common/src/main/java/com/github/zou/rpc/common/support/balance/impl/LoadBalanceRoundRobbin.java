package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮训策略
 * @author zou
 * @since 1.0.0
 */
public class LoadBalanceRoundRobbin extends AbstractLoadBalance {

    /**
     * 位移指针
     * @since 0.0.1
     */
    private final AtomicLong indexHolder = new AtomicLong();

    @Override
    protected IServer doSelect(ILoadBalanceContext context) {
        List<IServer> servers = context.servers();

        long index = indexHolder.getAndIncrement();
        int actual = (int) (index % servers.size());
        return servers.get(actual);
    }
}
