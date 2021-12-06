package com.github.zou;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * @author zou
 * @since 1.0.0
 */
public class CuratorDemo2 {

    public static void main(String[] args) throws Exception {
        String zkConnectionString = "localhost:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
        client.start();
        // 创建普通节点(默认是持久节点),内容为空
//        client.create().forPath("/t1");
        // 创建普通节点(默认是持久节点)
//        client.create().forPath("/erpc/1/2/3/4/5", "123456".getBytes());
        // 创建永久顺序节点,每次添加会自动累加t3000001-》t3000002
//        client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/t3", "123456".getBytes());
//        // 地柜创建，如果父节点不存在也会创建
        client.create().creatingParentContainersIfNeeded()
                .forPath("/e-rpc/server/testServiceImpl/127.0.0.1:9527:0","123454".getBytes());
    }

}
