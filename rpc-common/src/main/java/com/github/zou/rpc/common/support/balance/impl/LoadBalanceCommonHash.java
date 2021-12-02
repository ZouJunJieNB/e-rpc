package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;
import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.List;

/**
 * 普通 hash 策略
 * @author zou
 * @since 1.0.0
 */
public class LoadBalanceCommonHash extends AbstractLoadBalanceHash{

    public LoadBalanceCommonHash(IHashCode hashCode) {
        super(hashCode);
    }

    @Override
    protected IServer doSelect(ILoadBalanceContext context) {
        List<IServer> servers = context.servers();

        final String hashKey = context.hashKey();
        int code = hashCode.hash(hashKey);
        int hashCode = Math.abs(code);
        int index = hashCode % servers.size();
        return servers.get(index);
    }

}
