package com.github.zou.config.spring.beans.factory.util;

import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcReference;
import org.springframework.beans.factory.BeanCreationException;

import java.lang.annotation.Annotation;

/**
 * @author zou
 * @since 1.0.0
 */
public class AnnotationBuilderKeyUtil {

    public static String builderKey(Annotation annotation){
        if(annotation instanceof ERpcReference){
            ERpcReference erpcReference = (ERpcReference)annotation;
            String classStr = erpcReference.interfaceClass().toString();
            String name = erpcReference.serverId();
            long timeout = erpcReference.timeout();
            int callType = erpcReference.callType().code();
            int failType = erpcReference.failType().code();
            return classStr + name + timeout + callType + failType;

        }
        throw new BeanCreationException("@"+annotation.getClass().getName()+ " not found");

    }

}
