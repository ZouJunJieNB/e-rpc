package com.github.zou.config.spring.beans.factory.annotation;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.config.spring.StartRegister;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * @author zou
 * @since 1.0.0
 */
public abstract class AnnotationInjectedBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor,
        BeanFactoryAware {
    /**
     * ClientBs logger
     */
    private static final Log log = LogFactory.getLog(ReferenceAnnotationBeanPostProcessor.class);

    private ConfigurableListableBeanFactory beanFactory;

    private final Class<? extends Annotation>[] annotationTypes;


    public AnnotationInjectedBeanPostProcessor(Class<? extends Annotation>... annotationTypes) {
        Assert.notEmpty(annotationTypes, "The argument of annotations' types must not empty");
        this.annotationTypes = annotationTypes;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
                "AnnotationInjectedBeanPostProcessor requires a ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;

        StartRegister startRegister = beanFactory.getBean(StartRegister.class);
        if(startRegister.isStart()){
            return;
        }
        startRegister.startRegister();
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        return referencePostProcessProperties(pvs,bean,beanName);
    }

    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public Class<? extends Annotation>[] getAnnotationTypes() {
        return annotationTypes;
    }

    public final Class<? extends Annotation> getAnnotationType() {
        return annotationTypes[0];
    }

    protected abstract PropertyValues referencePostProcessProperties(PropertyValues pvs, Object bean, String beanName);
}
