package com.github.zou.config.spring.beans.factory.annotation;

import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.github.zou.config.spring.ClientApplicationConfig;
import com.github.zou.config.spring.ReferenceBean;
import com.github.zou.config.spring.beans.factory.annotation.context.annotation.ERpcReference;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.github.zou.config.spring.beans.factory.util.AnnotationBuilderKeyUtil.builderKey;
import static com.github.zou.rpc.common.util.Waits.waits;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} 的实现
 * 处理消费者服务{@link ERpcReference}注释的字段
 * @author zou
 * @since 1.0.0
 */
public class ReferenceAnnotationBeanPostProcessor extends AnnotationInjectedBeanPostProcessor {


    private static final Log log = LogFactory.getLog(ReferenceAnnotationBeanPostProcessor.class);

    private volatile boolean firstConnect = true;


    public static final String BEAN_NAME = "referenceAnnotationBeanPostProcessor";

    /**
     * Cache size
     */
    private static final int CACHE_SIZE = Integer.getInteger(BEAN_NAME + ".cache.size", 32);

    /**
     * todo 暂时将注解里面的各个属性合成作为唯一key，后期再优化
     */
    private final Map<String, ReferenceBean<?>> referenceBeanCache = new ConcurrentHashMap<>(CACHE_SIZE);

    @Resource
    private ClientApplicationConfig clientApplicationConfig;

    public ReferenceAnnotationBeanPostProcessor(){
        super(ERpcReference.class);
    }


    @Override
    protected PropertyValues referencePostProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        try {


            Class<?> clazz = bean.getClass();
            clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;

            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                Object proxy = null;
                for (Class<? extends Annotation> annotationType : getAnnotationTypes()) {
                    Annotation annotation = field.getAnnotation(annotationType);
                    if(annotation != null ){
                        Class<?> defaultInterface = field.getType();
                        proxy = getProxy(annotation,defaultInterface);
                        break;
                    }

                }

                if(proxy != null){
                    this.setFieldValue(field,bean,proxy);
                }
            }

        }catch (Exception e){
            throw new BeanCreationException(beanName, "Injection of @" + getAnnotationType().getSimpleName()
                    + " dependencies is failed", e);
        }

        return pvs;
    }

    private Object getProxy(Annotation annotation,Class<?> defaultInterface) {
        String cacheKey =  this.createCacheKey(annotation);
        ReferenceBean<?> referenceBean = referenceBeanCache.get(cacheKey);
        if(referenceBean == null){
            synchronized (this){
                firstConnect();
                referenceBeanCache.put(cacheKey,referenceBean = new ReferenceBean<>(annotation,defaultInterface,clientApplicationConfig));
            }
        }
        return referenceBean.getObject();
    }

    private void firstConnect() {
        if(firstConnect){

            // 等待100 ms，避免在注册中心启动之前连接报错
            waits(100, TimeUnit.MILLISECONDS);
            firstConnect = false;
        }
    }


    public String createCacheKey(Annotation annotation){
        return builderKey(annotation);
    }

    public void setFieldValue(Field field,Object bean,Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(bean,value);
    }


}
