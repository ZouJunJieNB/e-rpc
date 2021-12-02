package com.github.zou.rpc.common.support.balance.impl;

import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.List;

/**
 * @author zou
 * @since 1.0.0
 */
public class LoadBalanceContext implements ILoadBalanceContext {
    private String hashKey;

    /**
     * 服务端列表
     * @since 0.0.3
     */
    private List<IServer> servers;

    /**
     * 新建对象实例
     * @since 0.0.1
     * @return this
     */
    public static LoadBalanceContext newInstance() {
        return new LoadBalanceContext();
    }

    @Override
    public String hashKey() {
        return hashKey;
    }

    public LoadBalanceContext hashKey(String hashKey) {
        this.hashKey = hashKey;
        return this;
    }

    @Override
    public List<IServer> servers() {
        return servers;
    }

    public LoadBalanceContext servers(List<IServer> servers) {
        this.servers = servers;
        return this;
    }

    @Override
    public String toString() {
        return "LoadBalanceContext{" +
                "hashKey='" + hashKey + '\'' +
                ", servers=" + servers +
                '}';
    }
}
