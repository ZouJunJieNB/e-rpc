package com.github.zou;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;



/**
 * @author zou
 * @since 1.0.0
 */
public class Demo03 {
    public static void main(String[] args) throws Exception {
//        CuratorFrameworkFactory.Builder builder1 =
//                CuratorFrameworkFactory.builder()
//                        .connectString("localhost:8281")
//                        .sessionTimeoutMs(Integer.getInteger("curator-default-session-timeout", 60000))
//                        .connectionTimeoutMs(Integer.getInteger("curator-default-connection-timeout", 15000))
//                        .retryPolicy(new ExponentialBackoffRetry(1000, 1));
//        builder1 = builder1.authorization("digest", "1".getBytes());
//        CuratorFramework client = builder1.build();
//
//        client.getConnectionStateListenable().addListener((clients, state)->{
//        });
//
//        client.start();
//        client.create().forPath("/1234");


        String zkConnectionString = "localhost:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//        CuratorFramework client2 = CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);


        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        CuratorFramework client2 = builder.connectString(zkConnectionString).
                sessionTimeoutMs(Integer.getInteger("curator-default-session-timeout", 60 * 1000)).
                connectionTimeoutMs(Integer.getInteger("curator-default-connection-timeout", 15 * 1000)).
                retryPolicy(retryPolicy).
                build();
        client2.start();
        client2.create().forPath("/23455");
    }
}
