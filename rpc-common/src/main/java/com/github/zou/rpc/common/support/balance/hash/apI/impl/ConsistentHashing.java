package com.github.zou.rpc.common.support.balance.hash.apI.impl;

import com.github.zou.rpc.common.support.balance.hash.apI.IConsistentHashing;
import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zou
 * @since 1.0.0
 */
public class ConsistentHashing<T> implements IConsistentHashing<T> {

    /**
     * 虚拟节点数量
     * @since 0.0.1
     */
    private final int virtualNum;

    /**
     * hash 策略
     * @since 0.0.1
     */
    private final IHashCode hashCode;

    /**
     * node map 节点信息
     *
     * key: 节点 hash
     * Node: 节点
     * @since 0.0.1
     */
    private final TreeMap<Integer, T> nodeMap = new TreeMap<>();

    public ConsistentHashing(int virtualNum, IHashCode hashCode) {
        this.virtualNum = virtualNum;
        this.hashCode = hashCode;
    }

    /**
     * 沿环的顺时针找到虚拟节点
     * @param key key
     * @return 结果
     * @since 0.0.1
     */
    @Override
    public T get(String key) {
        final int hashCode = this.hashCode.hash(key);
        Integer target = hashCode;

        // 不包含时候的处理
        if (!nodeMap.containsKey(hashCode)) {
            target = nodeMap.ceilingKey(hashCode);
            if (target == null && !nodeMap.isEmpty()) {
                target = nodeMap.firstKey();
            }
        }
        return nodeMap.get(target);
    }

    @Override
    public IConsistentHashing add(T node) {
        // 初始化虚拟节点
        for (int i = 0; i < virtualNum; i++) {
            int nodeKey = this.hashCode.hash(node.toString() + "-" + i);
            nodeMap.put(nodeKey, node);
        }

        return this;
    }

    @Override
    public IConsistentHashing remove(T node) {
        // 移除虚拟节点
        // 其实这里有一个问题，如果存在 hash 冲突，直接移除会不会不够严谨？
        for (int i = 0; i < virtualNum; i++) {
            int nodeKey = this.hashCode.hash(node.toString() + "-" + i);
            nodeMap.remove(nodeKey);
        }

        return this;
    }

    @Override
    public Map<Integer, T> nodeMap() {
        return Collections.unmodifiableMap(this.nodeMap);
    }
}
