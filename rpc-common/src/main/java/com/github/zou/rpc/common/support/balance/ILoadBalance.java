package com.github.zou.rpc.common.support.balance;

import com.github.zou.rpc.common.support.balance.server.IServer;

/**
 * 轮询的顶级接口
 * @author zou
 * @since 1.0.0
 */
public interface ILoadBalance {

    /**
     * 选择下一个节点
     *
     * 返回下标
     * @param context 上下文
     * @return 结果
     */
    IServer select(final ILoadBalanceContext context);
}
