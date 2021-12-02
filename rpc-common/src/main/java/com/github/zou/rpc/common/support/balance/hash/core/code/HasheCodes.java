package com.github.zou.rpc.common.support.balance.hash.core.code;

import com.github.houbb.heaven.support.instance.impl.Instances;
import com.github.zou.rpc.common.support.balance.hash.apI.IHashCode;

/**
 * @author zou
 * @since 1.0.0
 */
public class HasheCodes {
    private HasheCodes(){}

    /**
     * crc 实现
     * @return 实现
     * @since 0.0.1
     */
    public static IHashCode crc() {
        return Instances.singleton(HashCodeCRC.class);
    }

    /**
     * fnv 实现
     * @return 实现
     * @since 0.0.1
     */
    public static IHashCode fnv() {
        return Instances.singleton(HashCodeFnv.class);
    }

    /**
     * fnv 实现
     * @return 实现
     * @since 0.0.1
     */
    public static IHashCode jdk() {
        return Instances.singleton(HashCodeJdk.class);
    }

    /**
     * ketama 实现
     * @return 实现
     * @since 0.0.1
     */
    public static IHashCode ketama() {
        return Instances.singleton(HashCodeKetama.class);
    }

    /**
     * murmur 实现
     * @return 实现
     * @since 0.0.1
     */
    public static IHashCode murmur() {
        return Instances.singleton(HashCodeMurmur.class);
    }

}
