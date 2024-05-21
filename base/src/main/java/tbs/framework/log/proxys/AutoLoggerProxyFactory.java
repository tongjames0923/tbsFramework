package tbs.framework.log.proxys;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.utils.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author abstergo
 */
public class AutoLoggerProxyFactory implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Field f : bean.getClass().getDeclaredFields()) {
            AutoLogger annotation = f.getDeclaredAnnotation(AutoLogger.class);
            if (annotation != null && f.getType().equals(ILogger.class)) {
                f.setAccessible(true);
                try {
                    Object fieldValue = f.get(bean);
                    if (fieldValue != null) {
                        continue;
                    }
                    String n = "";
                    LogFactory logFactory = null;
                    if (StrUtil.isEmpty(annotation.value())) {
                        n = bean.getClass().getName();
                    } else {
                        n = annotation.value();
                    }

                    Object proxyValue = createProxy(bean.getClass().getClassLoader(), n, annotation.factory());
                    f.set(bean, proxyValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to proxy field: " + f.getName(), e);
                }
            }
        }
        return bean;
    }

    private Object createProxy(ClassLoader classLoader, String name, String factoryName) {
        // 创建代理对象，可以使用动态代理或其他方式
        // 这里只是示例，具体实现根据需求来
        return Proxy.newProxyInstance(classLoader, new Class[] {ILogger.class}, new InvocationHandler() {
            private ILogger logger;

            private LogFactory factory = null;

            private LogFactory getLogFactory() {
                if (factory == null) {
                    if (StrUtil.isEmpty(factoryName)) {
                        factory = LogFactory.getInstance();
                    } else {
                        factory = SpringUtil.getBean(factoryName, LogFactory.class);
                    }
                }
                return factory;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (logger == null) {
                    logger = getLogFactory().getLogger(name);
                }
                return method.invoke(logger, args);
            }
        });
    }
}
