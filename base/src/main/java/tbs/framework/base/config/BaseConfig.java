package tbs.framework.base.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.aspects.LockAspect;
import tbs.framework.base.lock.impls.JdkLock;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.properties.BaseProperty;
import tbs.framework.base.properties.LockProperty;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.proxy.impls.LogExceptionProxy;
import tbs.framework.base.utils.IStartup;
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

/**
 * @author Abstergo
 */
public class BaseConfig {

    @Resource
    BaseProperty baseProperty;

    @Resource
    LockProperty lockProperty;

    ILogger logger;

    public ILogger logger() {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(BaseConfig.class.getName());
        }
        return logger;
    }

    @Bean
    ApplicationRunner startUp() {
        return args -> {
            for (IStartup startup : SpringUtil.getBeansOfType(IStartup.class).values()) {
                if (startup != null) {
                    logger().info("{} instance[{}] started", startup.getClass().getSimpleName(), startup);
                    startup.startUp();
                }
            }
        };
    }


    @Bean(name = BeanNameConstant.BUILTIN_LOGGER)
    public LogUtil getLogger()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (null == this.baseProperty.getLoggerProvider()) {
            return new Slf4jLoggerUtil();
        }
        return this.baseProperty.getLoggerProvider().getConstructor().newInstance();
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
                Lock l = lockMap.getOrDefault(s, new ReentrantLock());
                lockMap.put(s, l);
                return l;
            }
        }, util);
    }

    @Bean
    public UuidUtil uuidUtil() throws Exception {
        if (null == this.baseProperty.getUuidProvider()) {
            return new SimpleUuidUtil();
        }
        return baseProperty.getUuidProvider().getConstructor().newInstance();
    }


    @Bean(BeanNameConstant.BUILTIN_LOCK_PROXY)
    public LockProxy lockProxy(final LogUtil util) {
        return new LockProxy(this.lockProperty.getProxyLockType(), util, this.lockProperty.getProxyLockTimeout(),
            this.lockProperty.getProxyLockTimeUnit());
    }

    @Bean
    LockAspect lockAspect() {
        return new LockAspect();
    }
}
