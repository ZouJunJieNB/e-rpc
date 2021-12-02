package com.github.zou.rpc.common.support.balance.hash.core.code;

/**
 * FNV1_32_HASH 算法
 * @author zou
 * @since 1.0.0
 */
public class HashCodeFnv extends AbstractHashCode {

    /**
     * 初始化
     * @since 0.0.1
     */
    private static final long FNV_32_INIT = 2166136261L;

    /**
     * 质数
     * @since 0.0.1
     */
    private static final int FNV_32_PRIME = 16777619;

    @Override
    public int doHash(String text) {
        int hash = (int) FNV_32_INIT;
        for (int i = 0; i < text.length(); i++) {
            hash = (hash ^ text.charAt(i)) * FNV_32_PRIME;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        hash = Math.abs(hash);
        return hash;
    }
}
