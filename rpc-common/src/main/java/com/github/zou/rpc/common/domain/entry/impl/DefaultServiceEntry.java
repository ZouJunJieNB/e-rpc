
package com.github.zou.rpc.common.domain.entry.impl;


import com.github.zou.rpc.common.domain.entry.ServiceEntry;

/**
 * <p> 默认服务明细 </p>
 *
 * @author zou
 * @since 1.0.0
 */
class DefaultServiceEntry implements ServiceEntry {

    private static final long serialVersionUID = 189302740666003309L;

    /**
     * 服务标识
     */
    private String serviceId;

    /**
     * 服务描述
     */
    private String description;

    /**
     * 机器 ip 信息
     *
     * <pre>
     *     InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
     *     String clientIP = insocket.getAddress().getHostAddress();
     * </pre>
     *
     */
    private String ip;

    /**
     * 端口信息
     */
    private int port;

    /**
     * 权重信息
     */
    private int weight;

    /**
     * 是否临时
     */
    private boolean temporary;


    @Override
    public String serviceId() {
        return serviceId;
    }

    public DefaultServiceEntry serviceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    @Override
    public String description() {
        return description;
    }

    public DefaultServiceEntry description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String ip() {
        return ip;
    }

    public DefaultServiceEntry ip(String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public int port() {
        return port;
    }

    public DefaultServiceEntry port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public int weight() {
        return weight;
    }

    public DefaultServiceEntry weight(int weight) {
        this.weight = weight;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultServiceEntry{" +
                "serviceId='" + serviceId + '\'' +
                ", description='" + description + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultServiceEntry that = (DefaultServiceEntry) o;

        if (port != that.port) {
            return false;
        }
        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) {
            return false;
        }
        return ip != null ? ip.equals(that.ip) : that.ip == null;
    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }

    public boolean temporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
}
