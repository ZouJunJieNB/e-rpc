package com.github.zou.rpc.register.support.register.zookeeper.remoting;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.register.support.register.URL;

import java.util.List;

/**
 * @author zou
 * @since 1.0.0
 */
public abstract class AbstractZookeeperClient implements ZookeeperClient {


    private static final Log log = LogFactory.getLog(AbstractZookeeperClient.class);

    private final URL url;

    private volatile boolean closed = false;

    public AbstractZookeeperClient(URL url){
        this.url = url;
    }


    @Override
    public void create(String path, boolean ephemeral) {
        if (!ephemeral) {
            if (checkExists(path)) {
                return;
            }
        }
        int i = path.lastIndexOf('/');

        if (i > 0) {
            create(path.substring(0, i), false);
        }

        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
        }
    }

    @Override
    public URL getUrl() {
        return this.url;
    }


    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            doClose();
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
        }
    }

    /**
     * todo 此处bug待验证
     * @param path
     * @param content
     * @param ephemeral
     */
    @Override
    public void create(String path, String content, boolean ephemeral) {
        if (checkExists(path)) {
            delete(path);
        }

        int i = path.lastIndexOf('/');
        if (i > 0) {
            // todo 此处bug待验证
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path, content);
        } else {
            createPersistent(path, content);
        }
    }

    @Override
    public String getContent(String path) {
        if (!checkExists(path)) {
            return null;
        }
        return doGetContent(path);
    }

    protected abstract void doClose();

    protected abstract void createPersistent(String path);

    protected abstract void createEphemeral(String path);

    protected abstract void createPersistent(String path, String data);

    protected abstract void createEphemeral(String path, String data);

    protected abstract boolean checkExists(String path);

    protected abstract String doGetContent(String path);
}
