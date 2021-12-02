package com.github.zou;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;

/**
 * @author zou
 * @since 1.0.0
 */
public class CuratorDemo {

    //会话超时时间
    private static final int SESSION_TIMEOUT = 30 * 1000;

    //连接超时时间
    private static final int CONNECTION_TIMEOUT = 3 * 1000;

    //ZooKeeper服务地址
    private static final String CONNECT_ADDR = "localhost:2821";

    //创建连接实例
    private CuratorFramework client = null;

    public static void main(String[] args) throws Exception {
        String zkConnectionString = "localhost:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnectionString, retryPolicy);
        client.start();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(()->{
                try {

                    //创建分布式锁, 锁空间的根节点路径为  /curator/lock
                    InterProcessMutex lock = new InterProcessMutex(client, "/curator/lock");
                    // 获取锁等待多少时间，如超时就放弃
                    if ( lock.acquire(1, TimeUnit.SECONDS) )  {
                        try {
                            System.out.println(finalI+"拿到锁");
                            Thread.sleep(10000);
                            // do some work inside of the critical section here
                            System.out.println(finalI +"do some work inside of the critical section here");
                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                        finally{
                            //完成业务流程, 释放锁
                            lock.release();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }


    }
}
