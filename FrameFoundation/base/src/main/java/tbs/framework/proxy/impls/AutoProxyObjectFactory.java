package tbs.framework.proxy.impls;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.base.annotations.AutoProxy;
import tbs.framework.proxy.IAutoProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * @author abstergo
 */
@Slf4j
public class AutoProxyObjectFactory implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Field f : bean.getClass().getDeclaredFields()) {
            try {
                // 注意：这里需要谨慎使用
                f.setAccessible(true);
                Set<AutoProxy> proxies = AnnotatedElementUtils.getAllMergedAnnotations(f, AutoProxy.class);
                if (CollUtil.isEmpty(proxies)) {
                    continue;
                }
                for (AutoProxy proxy : proxies) {
                    IAutoProxy object = proxy.proxyImpl().getConstructor().newInstance();
                    if (!object.requiredType().isAssignableFrom(f.getType())) {
                        throw new RuntimeException("The type of field is not compatible with the proxy type");
                    }
                    object.wiredValue(f, bean);
                    f.set(bean,
                        Proxy.newProxyInstance(bean.getClass().getClassLoader(), new Class[] {object.requiredType()},
                            new AutoProxyInvocationHandler(object)));
                    break;
                }
            } catch (Exception e) {
                log.error("Error processing field: " + f.getName(), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    class AutoProxyInvocationHandler implements InvocationHandler {
        private IAutoProxy proxyObject;

        public AutoProxyInvocationHandler(IAutoProxy proxyObject) {
            this.proxyObject = proxyObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return proxyObject.proxyExecute(proxy, method, args);
        }
    }
}
