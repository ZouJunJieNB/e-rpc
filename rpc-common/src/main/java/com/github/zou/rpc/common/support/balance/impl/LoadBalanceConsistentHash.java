package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.hash.apI.IConsistentHashing;
import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;
import com.github.zou.rpc.common.support.balance.hash.bs.ConsistentHashingBs;
import com.github.zou.rpc.common.support.balance.server.IServer;

/**
 *  一致性 hash 策略
 *
 * @author zou
 * @since 1.0.0
 */
public class LoadBalanceConsistentHash  extends AbstractLoadBalanceHash {

    public LoadBalanceConsistentHash(IHashCode hashCode) {
        super(hashCode);
    }

    @Override
    protected IServer doSelect(ILoadBalanceContext context) {
        IConsistentHashing<IServer> consistentHashing = ConsistentHashingBs
                .<IServer>newInstance()
                .hashCode(hashCode)
                .nodes(context.servers())
                .build();

        final String hashKey = context.hashKey();
        return consistentHashing.get(hashKey);
    }
}
