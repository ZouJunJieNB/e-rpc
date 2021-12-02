package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalance;
import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.List;

/**
 * 轮询抽象类
 * @author zou
 * @since 1.0.0
 */
public abstract class AbstractLoadBalance implements ILoadBalance {

    @Override
    public IServer select(ILoadBalanceContext context) {
        List<IServer> servers = context.servers();

        if(servers.size() <= 1) {
            return servers.get(0);
        }

        return doSelect(context);
    }

    /**
     * 执行选择
     * @param context 上下文
     * @return 结果
     * @since 0.0.1
     */
    protected abstract IServer doSelect(final ILoadBalanceContext context);


}
