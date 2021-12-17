package com.github.zou.springserver;


import com.github.zou.TestService;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcService;

/**
 * @author zou
 * @since 1.0.0
 */
@ERpcService
public class TestServiceImpl implements TestService {

    @Override
    public String test() {
        return "test";
    }
}
