package com.github.zou.config.spring;

import com.github.zou.rpc.register.constant.enums.RegisterTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zou
 * @since 1.0.0
 */
@ConfigurationProperties(value = "e-rpc.application.register")
public class RegisterApplicationConfig {



    private String url;

    private String register;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RegisterTypeEnum getRegister() {
        return registerNameMaps.getOrDefault(register,RegisterTypeEnum.DEFAULT);
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public static String redis = "redis";
    public static String zookeeper = "zookeeper";
    public static String nacos = "nacos";

    private final static Map<String, RegisterTypeEnum> registerNameMaps = new HashMap<>();
    static {
        registerNameMaps.put(redis,RegisterTypeEnum.REDIS);
        registerNameMaps.put(zookeeper,RegisterTypeEnum.ZOOKEEPER);
        registerNameMaps.put(nacos,RegisterTypeEnum.NACOS);
    }
}
