package com.github.zou.rpc.register.support.register.zookeeper.remoting;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.register.support.register.URL;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author zou
 * @since 1.0.0
 */
public class CuratorZookeeperClient extends AbstractZookeeperClient {

    private static final Log log = LogFactory.getLog(CuratorZookeeperClient.class);

    private final CuratorFramework client;

    public CuratorZookeeperClient(URL url) {
        super(url);
        try {

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
            builder = builder.connectString(url.getAddress()).
                    sessionTimeoutMs(Integer.getInteger("curator-default-session-timeout", 60 * 1000)).
                    connectionTimeoutMs(url.getTimeout()).
                    retryPolicy(retryPolicy);


            // 是否需要身份认证
            String authority = url.getAuthority();
            if (authority != null && authority.length() > 0) {
                builder = builder.authorization("digest", authority.getBytes());
            }

            client = builder.build();

            client.getConnectionStateListenable().addListener((client, state)->{
                log.info("监听回调,状态为{}",state.toString());
            });

            client.start();

        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

    }

    @Override
    protected void doClose() {
        client.close();
    }

    @Override
    protected boolean checkExists(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return true;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return false;
    }

    @Override
    protected String doGetContent(String path) {
        try {
            byte[] dataBytes = client.getData().forPath(path);
            return (dataBytes == null || dataBytes.length == 0) ? null : new String(dataBytes, StandardCharsets.UTF_8);
        } catch (NoNodeException e) {
            // ignore NoNode Exception.
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return null;
    }


    @Override
    public void delete(String path) {
        try {
            client.delete().forPath(path);
        } catch (NoNodeException e) {
            // ignore NoNode Exception.
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }


    public void createPersistent(String path) {
        try {
            String s = client.create().forPath(path);
            System.out.println(s);
        } catch (NodeExistsException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    public void createEphemeral(String path) {
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (NodeExistsException e) {
            // ignore NoNode Exception.
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    protected void createPersistent(String path, String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        try {
            client.create().creatingParentContainersIfNeeded().forPath(path, dataBytes);
        } catch (NodeExistsException e) {
            try {
                client.setData().forPath(path, dataBytes);
            } catch (Exception e1) {
                throw new IllegalStateException(e.getMessage(), e1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    protected void createEphemeral(String path, String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, dataBytes);
        } catch (NodeExistsException e) {
            try {
                client.setData().forPath(path, dataBytes);
            } catch (Exception e1) {
                throw new IllegalStateException(e.getMessage(), e1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


}
