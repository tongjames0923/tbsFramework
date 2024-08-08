package tbs.framework.base.config;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.properties.BaseProperty;
import tbs.framework.base.properties.LockProperty;
import tbs.framework.base.properties.MqProperty;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.cache.managers.AbstractExpireManager;
import tbs.framework.lock.ILockProvider;
import tbs.framework.lock.aspects.LockAspect;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.mq.AbstractMessageHandleBlocker;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.consumer.manager.impls.MappedConsumerManager;
import tbs.framework.mq.impls.CacheMessageHandleBlocker;
import tbs.framework.proxy.impls.AutoProxyObjectFactory;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.utils.*;
import tbs.framework.utils.impls.SimpleUuidUtil;
import tbs.framework.utils.impls.Slf4JLoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

/**
 * @author Abstergo
 */
public class BaseConfig {

    @Resource
    BaseProperty baseProperty;

    @Resource
    LockProperty lockProperty;

    @Resource
    MqProperty mqProperty;

    @Bean
    SingletonHolder singletonHolder() {
        return new SingletonHolder();
    }

//    @Bean
//    @ConditionalOnMissingBean(IMessageQueueEvents.class)
//    IMessageQueueEvents baseMessageQueueEvent(IMessageConsumerManager manager) throws Exception {
//        if (mqProperty.getEventImpl() == null) {
//            return new EmptySentAndErrorEventImpl();
//        }
//        return mqProperty.getEventImpl().getConstructor().newInstance();
//    }

    @Bean
    ApplicationRunner startUp() {
        return new ApplicationRunner() {
            @AutoLogger
            ILogger logger;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                for (IStartup startup : SpringUtil.getBeansOfType(IStartup.class).values().stream().sorted((a1, a2) -> {
                    return a1.getOrder() - a2.getOrder();
                }).collect(Collectors.toList())) {
                    if (startup != null) {
                        logger.info("{} instance[{}] started", startup.getClass().getSimpleName(), startup);
                        startup.startUp();
                    }
                }
            }
        };
    }

    @Bean
    AutoProxyObjectFactory autoLoggerAspect() {
        return new AutoProxyObjectFactory();
    }

    @Bean
    LoggerUtils loggerUtils(@Qualifier(BeanNameConstant.BUILTIN_LOGGER) LogFactory logger) {
        return new LoggerUtils(logger);
    }

    @Bean(name = BeanNameConstant.BUILTIN_LOGGER)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public LogFactory getLogger()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        LogFactory factory = null;
        if (null == this.baseProperty.getLoggerProvider()) {
            factory = new Slf4JLoggerFactory();
        }
        factory = this.baseProperty.getLoggerProvider().getConstructor().newInstance();
        LogFactory.Companion.setInstance(factory);
        return factory;
    }

    @Bean
    public UuidUtil uuidUtil() throws Exception {
        if (null == this.baseProperty.getUuidProvider()) {
            return new SimpleUuidUtil();
        }
        return baseProperty.getUuidProvider().getConstructor().newInstance();
    }

    @Bean(BeanNameConstant.BUILTIN_LOCK_PROVIDER)
    public ILockProvider lockProvider() throws Exception {
        return this.lockProperty.getLockProvider().getConstructor().newInstance();
    }

    @Bean(BeanNameConstant.BUILTIN_LOCK_PROXY)
    public LockProxy lockProxy(@Qualifier(BeanNameConstant.BUILTIN_LOGGER) LogFactory util) {
        return new LockProxy(util, this.lockProperty.getProxyLockTimeout(), this.lockProperty.getProxyLockTimeUnit());
    }

    @Bean
    LockUtils lockUtils() {
        return new LockUtils();
    }

    @Bean
    LockAspect lockAspect() {
        return new LockAspect();
    }

    @Bean
    IMessageConsumerManager consumerManager() throws Exception {
        if (mqProperty.getConsumerManager() == null) {
            return new MappedConsumerManager();
        }
        return mqProperty.getConsumerManager().getConstructor().newInstance();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // You can add more configurations here if needed
        return mapper;
    }

    @ConditionalOnBean(AbstractExpireManager.class)
    @ConditionalOnMissingBean(AbstractMessageHandleBlocker.class)
    @Bean
    AbstractMessageHandleBlocker messageHandleBlocker() {

        return new CacheMessageHandleBlocker();
    }
}
