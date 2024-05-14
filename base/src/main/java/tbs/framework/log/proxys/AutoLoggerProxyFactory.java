package tbs.framework.log.proxys;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.utils.LogUtil;

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
                    if (StrUtil.isEmpty(annotation.value())) {
                        n = bean.getClass().getName();
                    } else {
                        n = annotation.value();
                    }
                    Object proxyValue = createProxy(bean.getClass().getClassLoader(), n);
                    f.set(bean, proxyValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to proxy field: " + f.getName(), e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private Object createProxy(ClassLoader classLoader, String name) {
        // 创建代理对象，可以使用动态代理或其他方式
        // 这里只是示例，具体实现根据需求来
        return Proxy.newProxyInstance(classLoader, new Class[] {ILogger.class}, new InvocationHandler() {
            private ILogger logger;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (logger == null) {
                    logger = LogUtil.getInstance().getLogger(name);
                }
                return method.invoke(logger, args);
            }
        });
    }
}
