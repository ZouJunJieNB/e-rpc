package com.github.zou.config.spring.beans.factory.annotation.context.annotation;

import com.github.zou.config.spring.ClientApplicationConfig;
import com.github.zou.config.spring.RegisterApplicationConfig;
import com.github.zou.config.spring.ServerApplicationConfig;
import com.github.zou.config.spring.StartRegister;
import com.github.zou.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.github.zou.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 *
 * @author zou
 * @since 1.0.0
 */
public class ERpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar{

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // AnnotationMetadata 表示导入类的注解元数据，可以获取该类上的注解信息以作用在自身类的信息
        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);

        registerServiceAnnotationBeanPostProcessor(packagesToScan, registry);
        // todo 这里将spring.factories 里面的类放在此处加载
        registerCommonBeans(registry);
    }


    private void registerCommonBeans(BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder clientApplicationConfigBuilder = rootBeanDefinition(ClientApplicationConfig.class);
        clientApplicationConfigBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionReaderUtils.registerWithGeneratedName(clientApplicationConfigBuilder.getBeanDefinition(), registry);

        BeanDefinitionBuilder registerApplicationConfigBuilder = rootBeanDefinition(RegisterApplicationConfig.class);
        registerApplicationConfigBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionReaderUtils.registerWithGeneratedName(registerApplicationConfigBuilder.getBeanDefinition(), registry);

        BeanDefinitionBuilder serverApplicationConfigBuilder = rootBeanDefinition(ServerApplicationConfig.class);
        registerApplicationConfigBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionReaderUtils.registerWithGeneratedName(serverApplicationConfigBuilder.getBeanDefinition(), registry);

        BeanDefinitionBuilder referenceAnnotationBeanPostProcessorBuilder = rootBeanDefinition(ReferenceAnnotationBeanPostProcessor.class);
        registerApplicationConfigBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionReaderUtils.registerWithGeneratedName(referenceAnnotationBeanPostProcessorBuilder.getBeanDefinition(), registry);

        BeanDefinitionBuilder startRegisterBuilder = rootBeanDefinition(StartRegister.class);
        registerApplicationConfigBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionReaderUtils.registerWithGeneratedName(startRegisterBuilder.getBeanDefinition(), registry);

    }

    /**
     * 注册 {@link ServiceAnnotationBeanPostProcessor}
     * @param packagesToScan
     * @param registry
     */
    private void registerServiceAnnotationBeanPostProcessor(Set<String> packagesToScan, BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceAnnotationBeanPostProcessor.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);

    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {

        // 获取注解的值
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(ERpcComponentScan.class.getName()));

        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        String[] value = attributes.getStringArray("value");

        // 每个属性进行追加到Set中
        Set<String> packagesToScan = new LinkedHashSet<String>(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }

        if (packagesToScan.isEmpty()) {
            // 如果注解里面属性包值为空，那么就取当前启动类默认的包名
            return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }



}
