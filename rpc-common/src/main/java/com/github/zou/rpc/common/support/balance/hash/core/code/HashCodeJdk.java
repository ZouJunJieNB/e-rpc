package com.github.zou.rpc.common.support.balance.hash.core.code;

/**
 * jdk 默认实现策略
 * @author zou
 * @since 1.0.0
 */
public class HashCodeJdk extends AbstractHashCode {

    @Override
    public int doHash(String text) {
        return text.hashCode();
    }
}
