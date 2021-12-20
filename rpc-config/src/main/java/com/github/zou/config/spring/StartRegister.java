package com.github.zou.config.spring;

import com.github.zou.rpc.register.core.RegisterBs;
import com.github.zou.rpc.register.support.register.URL;

import javax.annotation.Resource;

/**
 * @author zou
 * @since 1.0.0
 */
public class StartRegister  {

    @Resource
    private RegisterApplicationConfig registerApplicationConfig;

    private volatile boolean isStart = false;

    public boolean isStart(){
        synchronized (this){
            return isStart;
        }
    }

    public void startRegister(){
        URL url = new URL(registerApplicationConfig.getUrl());
        url.setPassword(registerApplicationConfig.getPassword());
        RegisterBs.newInstance().setTypeEnum(registerApplicationConfig.getType()).setUrl(url).start();
        isStart = true;
    }
}
