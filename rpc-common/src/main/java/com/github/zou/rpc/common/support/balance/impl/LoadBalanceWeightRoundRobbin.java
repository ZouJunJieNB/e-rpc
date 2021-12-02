package com.github.zou.rpc.common.support.balance.impl;

import com.github.houbb.heaven.support.filter.IFilter;
import com.github.houbb.heaven.support.handler.IHandler;
import com.github.houbb.heaven.util.lang.MathUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.zou.rpc.common.support.balance.ILoadBalanceContext;
import com.github.zou.rpc.common.support.balance.server.IServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 指定权重的轮训策略
 * @author zou
 * @since 1.0.0
 */
public class LoadBalanceWeightRoundRobbin extends AbstractLoadBalance {

    /**
     * 位移指针
     */
    private final AtomicLong indexHolder = new AtomicLong();

    @Override
    protected IServer doSelect(ILoadBalanceContext context) {
        List<IServer> servers = context.servers();
        List<IServer> actualList = buildActualList(servers);

        long index = indexHolder.getAndIncrement();

        // 基于真实的列表构建
        int actual = (int) (index % actualList.size());
        return actualList.get(actual);
    }

    /**
     * 初始化
     * @param serverList 服务列表
     */
    private List<IServer> buildActualList(final List<IServer> serverList) {
        final List<IServer> actualList = new ArrayList<>();

        //1. 过滤掉权重为 0 的机器
        List<IServer> notZeroServers = CollectionUtil.filterList(serverList, new IFilter<IServer>() {
            @Override
            public boolean filter(IServer iServer) {
                return iServer.weight() <= 0;
            }
        });

        //2. 获取权重列表
        List<Integer> weightList = CollectionUtil.toList(notZeroServers, new IHandler<IServer, Integer>() {
            @Override
            public Integer handle(IServer iServer) {
                return iServer.weight();
            }
        });

        //3. 获取最大的权重
        int maxDivisor = MathUtil.ngcd(weightList);

        //4. 重新计算构建基于权重的列表
        for(IServer server : notZeroServers) {
            int weight = server.weight();

            int times = weight / maxDivisor;
            for(int i = 0; i < times; i++) {
                actualList.add(server);
            }
        }

        return actualList;
    }

}
