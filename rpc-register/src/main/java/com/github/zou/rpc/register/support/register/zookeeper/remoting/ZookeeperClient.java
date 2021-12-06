package com.github.zou.rpc.register.support.register.zookeeper.remoting;

import com.github.zou.rpc.register.support.register.URL;

import java.util.List;

/**
 * @author zou
 * @since 1.0.0
 */
public interface ZookeeperClient {

    void create(String path, boolean ephemeral);

    void create(String path, String content, boolean ephemeral);

    void delete(String path);

    List<String> getChildren(String path);

    boolean isConnected();

    void close();

    String getContent(String path);

    URL getUrl();
}
