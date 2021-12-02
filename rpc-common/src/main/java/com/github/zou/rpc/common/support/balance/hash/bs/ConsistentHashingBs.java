package com.github.zou.rpc.common.support.balance.hash.bs;

import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.zou.rpc.common.support.balance.hash.apI.IConsistentHashing;
import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;
import com.github.zou.rpc.common.support.balance.hash.apI.impl.ConsistentHashing;
import com.github.zou.rpc.common.support.balance.hash.core.code.HasheCodes;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author zou
 * @since 1.0.0
 */
public class ConsistentHashingBs<T> {

    public ConsistentHashingBs() {
    }

    /**
     * 新建对象实例
     * @param <T> 泛型
     * @return 结果
     * @since 0.0.1
     */
    public static <T> ConsistentHashingBs<T> newInstance() {
        return new ConsistentHashingBs<>();
    }

    /**
     * 虚拟节点数
     * @since 0.0.1
     */
    private int virtualNum = 16;

    /**
     * hash 实现策略
     * @since 0.0.1
     */
    private IHashCode hashCode = HasheCodes.jdk();

    /**
     * 设置节点
     * @since 0.0.1
     */
    private Collection<T> nodes = new HashSet<>();

    /**
     * 设置虚拟节点数量
     * @param virtualNum 虚拟节点
     * @return 结果
     * @since 0.0.1
     */
    public ConsistentHashingBs<T> virtualNum(int virtualNum) {
        ArgUtil.gt("virtualNum", virtualNum, 0);

        this.virtualNum = virtualNum;
        return this;
    }

    /**
     * 设置 hash 策略
     *
     * 暂时不开放
     * @param hashCode hashCode 策略
     * @return 结果
     * @since 0.0.1
     */
    public ConsistentHashingBs<T> hashCode(IHashCode hashCode) {
        ArgUtil.notNull(hashCode, "hashCode");

        this.hashCode = hashCode;
        return this;
    }

    /**
     * 设置初始化节点
     * @param nodes 节点
     * @return 结果
     * @since 0.0.1
     */
    public ConsistentHashingBs<T> nodes(Collection<T> nodes) {
        ArgUtil.notEmpty(nodes, "nodes");

        this.nodes = nodes;
        return this;
    }

    /**
     * 构建结果
     * @return 实现
     * @since 0.0.1
     */
    public IConsistentHashing<T> build() {
        IConsistentHashing<T> hashing = new ConsistentHashing<>(virtualNum, hashCode);

        if(CollectionUtil.isNotEmpty(nodes)) {
            for(T node : nodes) {
                hashing.add(node);
            }
        }
        return hashing;
    }
}
