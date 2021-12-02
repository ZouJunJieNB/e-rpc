package com.github.zou.rpc.common.support.balance.hash.apI;

import java.util.Map;

/**
 * @author zou
 * @since 1.0.0
 */
public interface IConsistentHashing<T> {

    /**
     * 获取对应的节点
     * @param key key
     * @return 节点
     * @since 0.0.1
     */
    T get(final String key);

    /**
     * 添加节点
     * @param node 节点
     * @return this
     * @since 0.0.1
     */
    IConsistentHashing add(final T node);

    /**
     * 移除节点
     * @param node 节点
     * @return this
     * @since 0.0.1
     */
    IConsistentHashing remove(final T node);

    /**
     * 获取节点信息
     * @return 节点
     * @since 0.0.1
     */
    Map<Integer, T> nodeMap();
}
