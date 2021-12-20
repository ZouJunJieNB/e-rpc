package com.github.zou.config.spring.beans.factory.annotation;

import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.config.spring.ServerApplicationConfig;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcClassPathBeanDefinitionScanner;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcComponentScanRegistrar;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcService;
import com.github.zou.rpc.server.core.ServiceBs;
import com.github.zou.rpc.server.registry.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.github.zou.rpc.common.util.IpUtils.registerAddress;
import static com.github.zou.rpc.common.util.Waits.waits;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 *
 * 处理 {@link ERpcService} 注解
 *
 * 实现了 {@link BeanDefinitionRegistryPostProcessor} 接口，实现了postProcessBeanDefinitionRegistry方法。
 * 此方法的介绍是:
 * 在标准初始化之后修改应用程序上下文的内部bean定义注册表。所有常规bean定义都已加载，但尚未实例化任何bean。这允许在下一个后处理阶段开始之前添加更多的bean定义。
 * 也就是说在所有BeanDefinition已经加载完的时候，执行此方法。允许我们再加入自定义的BeanDefinition到BeanDefinitionRegistry中。这时候就是我们扫描自己的bean加入的好时机
 * 并且此类实现了 {@link BeanFactoryPostProcessor} 接口，我们还得实现它的postProcessBeanFactory方法，此方法的介绍是:
 * 在标准初始化之后修改应用程序上下文的内部bean工厂。所有bean定义都已加载，但尚未实例化任何bean。这允许重写或添加属性，甚至可以初始化bean。但是我们这里没有对它进行任何操作
 *
 * SmartInitializingSingleton 的 afterSingletonsInstantiated方法是在所有单例bean创建完成时调用的
 *
 * @author zou
 * @since 1.0.0
 */
public class ServiceAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware, BeanFactoryAware, SmartInitializingSingleton {

    private static final Log log = LogFactory.getLog(ServiceAnnotationBeanPostProcessor.class);

    private Environment environment;

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    private ConfigurableListableBeanFactory beanFactory;

    /**
     * The bean name of {@link ServiceAnnotationBeanPostProcessor}
     */
    public static final String BEAN_NAME = "serviceAnnotationBeanPostProcessor";

    /**
     * Cache size
     */
    private static final int CACHE_SIZE = Integer.getInteger(BEAN_NAME + ".cache.size", 32);

    private final Set<String> packagesToScan;

    private final Map<String,String> rpcRelativeSpringMap = new ConcurrentHashMap<>(CACHE_SIZE);

    /**
     * 这里的初始化通过 {@link ERpcComponentScanRegistrar} 完成
     * @param packagesToScan 路径
     */
    public ServiceAnnotationBeanPostProcessor(Set<String> packagesToScan){
        this.packagesToScan = packagesToScan;
    }



    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 解析扫描的包
        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);
        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerServiceBeans(resolvedPackagesToScan, registry);
            return;
        }
        log.warn("packagesToScan is empty , ServiceBean registry will be ignored!");

    }

    /**
     * 将加了{@link ERpcService}的类进行注册
     *
     * @param packagesToScan 要扫描的基本包
     * @param registry       {@link BeanDefinitionRegistry}
     */
    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {

        ERpcClassPathBeanDefinitionScanner scanner = new ERpcClassPathBeanDefinitionScanner(registry, environment, resourceLoader);

        // 创建bean名称生成器
        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        // 向扫描器里面添加bean名称生成器
        scanner.setBeanNameGenerator(beanNameGenerator);

        // 添加扫描过滤条件
        scanner.addIncludeFilter(new AnnotationTypeFilter(ERpcService.class));


        for (String packageToScan : packagesToScan) {

            // 扫描此路径下的所有符合过滤的类注册为BeanDefinition加入spring
            scanner.scan(packageToScan);

            registerCache(packageToScan,scanner,beanNameGenerator,registry);

        }


    }

    private void registerCache(String packageToScan, ERpcClassPathBeanDefinitionScanner scanner,
                               BeanNameGenerator beanNameGenerator, BeanDefinitionRegistry registry){
        // 准备暴露服务
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String serverId = null;
            if(beanDefinition instanceof AnnotatedBeanDefinition){
                AnnotatedBeanDefinition annotatedBeanDefinition =  (AnnotatedBeanDefinition)beanDefinition;
                AnnotationMetadata metadata = annotatedBeanDefinition.getMetadata();
                String[] interfaceNames = metadata.getInterfaceNames();
                if(interfaceNames.length >= 1){
                    serverId = interfaceNames[0];
                }
            }
            ERpcService serviceAnnotation = findServiceAnnotation(resolveClass(beanDefinition));
            String annotationServerId = serviceAnnotation.serverId();
            if(StringUtil.isNotEmpty(annotationServerId)){
                serverId = annotationServerId;
            }
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            rpcRelativeSpringMap.put(serverId,beanName);
        }
    }

    /**
     * Find the {@link Annotation annotation} of @Service
     *
     * @param beanClass the {@link Class class} of Bean
     * @return <code>null</code> if not found
     * @since 2.7.3
     */
    private ERpcService findServiceAnnotation(Class<?> beanClass) {
        return findMergedAnnotation(beanClass, ERpcService.class);
    }

    private Class<?> resolveClass(BeanDefinition beanDefinition) {

        String beanClassName = beanDefinition.getBeanClassName();

        return resolveClassName(beanClassName, classLoader);

    }



    /**
     * 最好使用应引用ConfigurationClassPostProcessor的BeanNameGenerator实例。，
     * 因此，它可能是bean名称生成中的一个潜在问题。
     *
     * @param registry {@link BeanDefinitionRegistry}
     * @return {@link BeanNameGenerator} instance
     * @see SingletonBeanRegistry
     * @since 2.5.8
     */
    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {
            beanNameGenerator = new AnnotationBeanNameGenerator();

        }

        return beanNameGenerator;

    }

    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
                "AnnotationInjectedBeanPostProcessor requires a ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 这里使用beanFactory获取的原因是此类实现了BeanDefinitionRegistryPostProcessor,在所有bean实例化之前就创建了，获取不到。
        // 但是此方法是所有bean实例化后调用的，所以在此是可以获取到bean的
        ServerApplicationConfig serverApplicationConfig = beanFactory.getBean(ServerApplicationConfig.class);
        ServiceRegistry serviceRegistry = ServiceBs.getInstance(serverApplicationConfig.getPort()).registerCenter(registerAddress());

        // 在所有spring单例bean创建完成后暴露rpc服务
        long delayInMills = serverApplicationConfig.getDelayInMills();
        rpcRelativeSpringMap.forEach((k,v)->{
            Object bean = beanFactory.getBean(v);
            serviceRegistry.register(k,bean,delayInMills);
        });

        if(!serviceRegistry.isExpose()){
            return;
        }
        // 等待100 ms，避免在注册中心启动之前连接报错
        waits(100, TimeUnit.MILLISECONDS);
        serviceRegistry.expose();

    }
}
