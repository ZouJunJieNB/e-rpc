package com.github.zou.rpc.common.support.balance;

import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.List;

/**
 * @author zou
 * @since 1.0.0
 */
public interface ILoadBalanceContext {
    /**
     * 调用的 hashKey
     * @return hashKey
     */
    String hashKey();

    /**
     * 服务端列表
     * @return 列表
     */
    List<IServer> servers();
}
