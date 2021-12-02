package com.github.zou.rpc.common.support.balance.hash.apI;

/**
 * @author zou
 * @since 1.0.0
 */
public interface IHashCode {

    /**
     * 计算 hash 值
     * @param text 文本
     * @return 结果
     * @since 0.0.1
     */
    int hash(String text);
}
