package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;

/**
 * @author zou
 * @since 1.0.0
 */
public abstract class AbstractLoadBalanceHash extends AbstractLoadBalance{

    /**
     * hash 策略
     */
    protected final IHashCode hashCode;

    public AbstractLoadBalanceHash(IHashCode hashCode) {
        this.hashCode = hashCode;
    }
}
