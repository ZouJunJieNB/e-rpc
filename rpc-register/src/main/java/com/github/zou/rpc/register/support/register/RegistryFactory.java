package com.github.zou.rpc.register.support.register;

import com.github.zou.rpc.common.domain.entry.ServiceEntry;
import com.github.zou.rpc.register.constant.enums.RegisterTypeEnum;
import com.github.zou.rpc.register.simple.client.RegisterClientService;
import com.github.zou.rpc.register.simple.server.RegisterServerService;
import com.github.zou.rpc.register.spi.RpcRegistry;
import com.github.zou.rpc.register.support.register.zookeeper.ZookeeperRegistry;

import java.util.Collections;
import java.util.List;

/**
 * @author zou
 * @since 1.0.0
 */
public class RegistryFactory {

    private final RpcRegistry rpcRegister;

    public RegistryFactory(URL url, RegisterTypeEnum type, RegisterServerService registerServerService, RegisterClientService registerClientService){


        switch (type){

            case ZOOKEEPER:
                checkUrl(url);
                rpcRegister = new ZookeeperRegistry(url,registerServerService,registerClientService);
                break;
            default:
                rpcRegister = new AbstractRpcRegistry(registerServerService,registerClientService) {
                    @Override
                    public List<ServiceEntry> doLookUp(String serviceId) {
                        return Collections.emptyList();
                    }
                };
        }
    }

    void checkUrl(URL url){
        if(url == null){
            throw new IllegalArgumentException("url Cannot be empty");
        }
    }

    public RpcRegistry getRpcRegister(){
        return rpcRegister;
    }

}
