package com.github.zou.rpc.common.support.balance.hash.core.code;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zou
 * @since 1.0.0
 */
public class HashCodeKetama extends AbstractHashCode {

    private static MessageDigest md5Digest;

    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
    }

    @Override
    public int doHash(String origin) {
        byte[] bKey = computeMd5(origin);
        long rv = ((long) (bKey[3] & 0xFF) << 24)
                | ((long) (bKey[2] & 0xFF) << 16)
                | ((long) (bKey[1] & 0xFF) << 8)
                | (bKey[0] & 0xFF);
        return (int) (rv & 0xffffffffL);
    }

    /**
     * 给定md5的Get键。
     */
    private static byte[] computeMd5(String k) {
        MessageDigest md5;
        try {
            md5 = (MessageDigest) md5Digest.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone of MD5 not supported", e);
        }
        md5.update(k.getBytes());
        return md5.digest();
    }

}
