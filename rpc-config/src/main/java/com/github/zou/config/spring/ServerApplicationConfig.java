package com.github.zou.config.spring;

import com.github.houbb.heaven.util.lang.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.zou.rpc.common.constant.PunctuationConst.COMMA;


/**
 * @author zou
 * @since 1.0.0
 */
@ConfigurationProperties(value = "e-rpc.application.server")
public class ServerApplicationConfig {

    /**
     * 服务端口号
     */
    private int port;

    /**
     * 延迟暴露时间
     */
    private long delayInMills;

    /**
     * 需要暴露的包名，逗号分隔
     */
    private String packagesScan;

    public int getPort() {
        if (port == 0) {
            throw new IllegalArgumentException("Please specify eRpc server port ");
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getDelayInMills() {
        return delayInMills;
    }

    public void setDelayInMills(long delayInMills) {
        this.delayInMills = delayInMills;
    }

    public Set<String> getPackagesScan() {
        if(StringUtil.isEmpty(packagesScan)){
            throw new IllegalArgumentException("Please specify packagesScan ");
        }
        return new LinkedHashSet<>(Arrays.asList(packagesScan.split(COMMA)));
    }

    public void setPackagesScan(String packagesScan) {
        this.packagesScan = packagesScan;
    }
}
