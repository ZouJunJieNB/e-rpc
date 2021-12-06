package com.github.zou.rpc.register.constant.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 注册中心类型
 * @author zou
 * @since 1.0.0
 */
public enum RegisterTypeEnum {

    /**
     * redis
     */
    REDIS(1),

    /**
     * zookeeper
     */
    ZOOKEEPER(2),

    /**
     * nacos
     */
    NACOS(3),

    /**
     * 默认内存
     */
    DEFAULT(4),
    ;

    private final static Map<Integer,String> nameMaps = Maps.newHashMap();
    static {
        nameMaps.put(REDIS.code,"redis");
        nameMaps.put(ZOOKEEPER.code,"zookeeper");
        nameMaps.put(NACOS.code,"nacos");
        nameMaps.put(DEFAULT.code,"local");
    }

    public String getName(){
        return nameMaps.get(this.code);
    }

    private final int code;

    RegisterTypeEnum(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    @Override
    public String toString() {
        return "CallTypeEnum{" +
                "code=" + code +
                '}';
    }
}
