package tbs.framework.base.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.aspects.LockAspect;
import tbs.framework.base.lock.impls.JdkLock;
import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.log.impls.Slf4jLoggerProvider;
import tbs.framework.base.properties.BaseProperty;
import tbs.framework.base.properties.LockProperty;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.proxy.impls.LogExceptionProxy;
import tbs.framework.base.utils.LogUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

@Order(0)
public class BaseConfig {

    @Resource
    BaseProperty baseProperty;

    @Resource
    LockProperty lockProperty;

    @Bean(name = BeanNameConstant.BUILTIN_LOGGER)
    @Order(0)
    public ILogProvider getLogger() {
        if (null == baseProperty.getLoggerProvider()) {
            return new Slf4jLoggerProvider();
        }
        return SpringUtil.getBean(this.baseProperty.getLoggerProvider());
    }

    @Bean
    @Order(10)
    public LogUtil getLogUtil() {
        return new LogUtil();
    }


    @Bean(BeanNameConstant.ERROR_LOG_PROXY)
    public IProxy logErrorProxy(final LogUtil util) {
        return new LogExceptionProxy(util);
    }


    @Bean(BeanNameConstant.BUILTIN_JDK_LOCK)
    @ConditionalOnMissingBean(ILock.class)
    public ILock builtinJdkLock(final LogUtil util) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return new JdkLock(this.lockProperty.getLockImpl().getConstructor().newInstance(), util);
    }

    @Bean(BeanNameConstant.BUILTIN_LOCK_PROXY)
    @ConditionalOnMissingBean(LockProxy.class)
    public LockProxy lockProxy(final ILock lock, final LogUtil util) {
        return new LockProxy(lock, util, this.lockProperty.getLockTimeout(), this.lockProperty.getLockTimeUnit());
    }

    @Bean
    LockAspect lockAspect() {
        return new LockAspect();
    }
}
