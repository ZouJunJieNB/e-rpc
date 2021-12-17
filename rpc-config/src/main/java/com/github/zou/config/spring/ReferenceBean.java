package com.github.zou.config.spring;

import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcReference;
import com.github.zou.rpc.client.core.AbstractReferenceConfig;
import com.github.zou.rpc.client.support.fail.enums.FailTypeEnum;
import com.github.zou.rpc.common.constant.enums.CallTypeEnum;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;

import java.lang.annotation.Annotation;

import static com.github.zou.rpc.common.constant.CommonsConst.defaultTimeout;
import static com.github.zou.rpc.common.util.IpUtils.registerAddress;

/**
 *
 * 此FactoryBean不加入spring bean管理，只是作为一个工具容器
 *
 * @author zou
 * @since 1.0.0
 */
public class ReferenceBean<T> extends AbstractReferenceConfig<T> implements FactoryBean<T> {

    private T reference;

    public ReferenceBean(Annotation annotation, Class<T> defaultInterface, ClientApplicationConfig clientApplicationConfig){
        super();
        init(annotation,defaultInterface,clientApplicationConfig);
    }

    @Override
    public T getObject(){
        synchronized (this) {
            return getProxy();
        }

    }

    @Override
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    private T getProxy(){
        if(reference != null){
            return reference;
        }

        return reference = reference();
    }

    public ReferenceBean<T> init(Annotation annotation, Class<T> defaultInterface, ClientApplicationConfig clientApplicationConfig){
        if(annotation instanceof ERpcReference){
            ERpcReference erpcReference = (ERpcReference)annotation;

            String name = erpcReference.serverId();
            if(StringUtil.isNotEmpty(name)){
                serviceId(name);
            }else{
                serviceId(defaultInterface.getName());
            }

            // 全局配置和局部配置的选择
            long timeout = erpcReference.timeout();
            if(timeout != defaultTimeout){
                timeout(timeout);
            }else{
                long applicationTimeout = clientApplicationConfig.getTimeout();
                if (0 != applicationTimeout) {
                    timeout(applicationTimeout);
                }
            }

            CallTypeEnum callTypeEnum = erpcReference.callType();
            if(callTypeEnum != CallTypeEnum.SYNC){
                callType(callTypeEnum);
            }else{
                CallTypeEnum callType = clientApplicationConfig.getCallType();
                if(null != callType){
                    callType(callType);
                }
            }

            FailTypeEnum failTypeEnum = erpcReference.failType();
            if(callTypeEnum != CallTypeEnum.SYNC){
                failType(failTypeEnum);
            }else{
                FailTypeEnum failType = clientApplicationConfig.getFailType();
                if(null != failType){
                    failType(failType);
                }
            }

            serviceInterface(defaultInterface);
            registerCenter(registerAddress());
            return this;
        }
        throw new BeanCreationException("@"+annotation.getClass().getName()+ " not found");
    }



}
