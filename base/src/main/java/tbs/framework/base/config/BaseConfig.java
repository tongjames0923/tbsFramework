package tbs.framework.base.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.aspects.LockAspect;
import tbs.framework.base.lock.impls.JdkLock;
import tbs.framework.base.properties.BaseProperty;
import tbs.framework.base.properties.LockProperty;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.proxy.impls.LogExceptionProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.UuidUtil;
import tbs.framework.base.utils.impls.SimpleUuidUtil;
import tbs.framework.base.utils.impls.Slf4jLoggerUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class BaseConfig {

    @Resource
    BaseProperty baseProperty;

    @Resource
    LockProperty lockProperty;

    @Bean(name = BeanNameConstant.BUILTIN_LOGGER)
    public LogUtil getLogger() {
        if (null == this.baseProperty.getLoggerProvider()) {
            return new Slf4jLoggerUtil();
        }
        return SpringUtil.getBean(this.baseProperty.getLoggerProvider());
    }

    @Bean(BeanNameConstant.ERROR_LOG_PROXY)
    public IProxy logErrorProxy(final LogUtil util) {
        return new LogExceptionProxy(util);
    }

    @Bean(BeanNameConstant.BUILTIN_LOCK)
    public ILock builtinJdkLock(final LogUtil util)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return new JdkLock(new Function<String, Lock>() {
            private Map<String, Lock> lockMap = new HashMap<>();

            @Override
            public Lock apply(String s) {
                if (lockMap.containsKey(s)) {
                    return lockMap.get(s);
                } else {
                    Lock l = new ReentrantLock();
                    lockMap.put(s, l);
                    return l;
                }
            }
        }, util);
    }

    @Bean
    public UuidUtil uuidUtil() {
        if (null == this.baseProperty.getUuidProvider()) {
            return new SimpleUuidUtil();
        }
        return SpringUtil.getBean(baseProperty.getUuidProvider());
    }

    @Bean(BeanNameConstant.BUILTIN_LOCK_PROXY)
    public LockProxy lockProxy(final LogUtil util) {
        return new LockProxy(this.lockProperty.getLockType(), util, this.lockProperty.getLockTimeout(),
            this.lockProperty.getLockTimeUnit());
    }

    @Bean
    LockAspect lockAspect() {
        return new LockAspect();
    }
}
