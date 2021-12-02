package com.github.zou.rpc.common.support.balance.hash.core.code;

import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;

/**
 * @author zou
 * @since 1.0.0
 */
public abstract class AbstractHashCode implements IHashCode {
    @Override
    public int hash(String text) {
        if(null == text) {
            return 0;
        }

        return doHash(text);
    }

    /**
     * 执行真正的 hash 策略
     * @param text 文本
     * @return 结果
     */
    protected abstract int doHash(final String text);
}
