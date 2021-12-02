package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalance;
import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;

/**
 * 选择轮询工具类
 * @author zou
 * @since 1.0.0
 */
public final class LoadBalances {
    private LoadBalances(){}

    /**
     * 随机
     * @return 结果
     * @since 0.0.1
     */
    public static ILoadBalance random() {
        return new LoadBalanceRandom();
    }

    /**
     * 轮训
     * @return 结果
     * @since 0.0.1
     */
    public static ILoadBalance roundRobbin() {
        return new LoadBalanceRoundRobbin();
    }

    /**
     * 权重轮训
     * @return 结果
     * @since 0.0.1
     */
    public static ILoadBalance weightRoundRobbin() {
        return new LoadBalanceWeightRoundRobbin();
    }

    /**
     * 普通 Hash
     * @param hashCode 哈希策略
     * @return 结果
     * @since 0.0.1
     */
    public static ILoadBalance commonHash(final IHashCode hashCode) {
        return new LoadBalanceCommonHash(hashCode);
    }

    /**
     * 一致性 Hash
     * @param hashCode 哈希策略
     * @return 结果
     * @since 0.0.1
     */
    public static ILoadBalance consistentHash(final IHashCode hashCode) {
        return new LoadBalanceConsistentHash(hashCode);
    }
}

