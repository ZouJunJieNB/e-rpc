package com.github.zou.rpc.common.support.generic.impl;


import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.rpc.common.exception.GenericException;
import com.github.zou.rpc.common.support.generic.GenericService;

import java.util.Arrays;

/**
 * 最简单的泛化调用实现
 *
 * @author zou
 * @since 1.0.0
 */
public final class FooGenericService implements GenericService {

    private static final Log LOG = LogFactory.getLog(FooGenericService.class);

    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        LOG.info("[Generic] method: {}", method);
        LOG.info("[Generic] parameterTypes: {}", Arrays.toString(parameterTypes));
        LOG.info("[Generic] args: {}", args);
        return null;
    }

}
