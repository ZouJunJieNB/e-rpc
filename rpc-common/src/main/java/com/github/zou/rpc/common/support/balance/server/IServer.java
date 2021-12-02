package com.github.zou.rpc.common.support.balance.server;

/**
 * @author zou
 * @since 1.0.0
 */
public interface IServer {


    /**
     * 地址信息
     * @return 地址
     */
    String url();

    /**
     * 权重
     * @return 权重
     */
    int weight();

}
