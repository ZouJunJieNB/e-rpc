package com.github.zou.rpc.register.support.register;

import com.github.houbb.heaven.util.common.ArgUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;

import static com.github.zou.rpc.common.constant.PunctuationConst.*;
import static com.github.zou.rpc.common.constant.PunctuationConst.SLASH;

/**
 * 扩展注册
 * @author zou
 * @since 1.0.0
 */
public abstract class ExtendRegistry extends FailBackRegistry {

    private final String SERVER = "server";

    private final String CLIENT = "client";

    private String root;

    public ExtendRegistry(RegisterServerService registerServerService, RegisterClientService registerClientService) {
        super(registerServerService, registerClientService);
    }

    public String server() {
        return SERVER;
    }

    public String client() {
        return CLIENT;
    }

    protected String toUrlPath(ServiceEntry serviceEntry, String head) {
        return toRootDir() + head+ SLASH + serviceEntry.serviceId() + splicingNode(serviceEntry);
    }

    protected String toKeyPath(String serviceId, String head) {
        return toRootDir() + head+ SLASH + serviceId;
    }

    protected String splicingNode(ServiceEntry serviceEntry){
        String ip = serviceEntry.ip();
        if(StringUtil.isEmpty(ip)){
            return EMPTY;
        }
        return SLASH +ip+ COLON +serviceEntry.port()+ COLON +serviceEntry.weight();
    }

    protected String splicingNodeNoSlash(ServiceEntry serviceEntry){
        String ip = serviceEntry.ip();
        if(StringUtil.isEmpty(ip)){
            return EMPTY;
        }
        return  ip+ COLON +serviceEntry.port()+ COLON +serviceEntry.weight();
    }

    protected String toRootDir() {
        ArgUtil.notNull(root,"root dir not null");
        if (root.equals(SLASH)) {
            return root;
        }

        if (!root.endsWith(SLASH)) {
            return root + SLASH;
        }
        return root ;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
