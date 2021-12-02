package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机策略
 * @author zou
 * @since 1.0.0
 */
public class LoadBalanceRandom extends AbstractLoadBalance{

    @Override
    protected IServer doSelect(ILoadBalanceContext context) {
        List<IServer> servers = context.servers();

        Random random = ThreadLocalRandom.current();
        int nextIndex = random.nextInt(servers.size());
        return servers.get(nextIndex);
    }
}
