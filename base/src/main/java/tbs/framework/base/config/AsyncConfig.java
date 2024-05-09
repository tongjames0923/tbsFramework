package tbs.framework.base.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.properties.ExecutorProperty;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.ThreadUtil;
import tbs.framework.base.utils.impls.SimpleThreadUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author abstergo
 */
public class AsyncConfig {
    @Resource
    ExecutorProperty executorProperty;

    private final ILogger logger;

    AsyncConfig(final LogUtil util) {
        this.logger = util.getLogger(AsyncConfig.class.getName());
        this.logger.trace("Async config initialized");
    }


    @Bean(name = BeanNameConstant.ASYNC_EXECUTOR)
    public ExecutorService asyncExecutor()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return new ThreadPoolExecutor(this.executorProperty.getCorePoolSize(), this.executorProperty.getMaxPoolSize(),
            this.executorProperty.getKeepAliveTime(), TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(this.executorProperty.getQueueCapacity()),
            new ThreadFactoryBuilder().setNamePrefix("main-pool-").setUncaughtExceptionHandler(
                this.executorProperty.getUncaughtExceptionHandler().getConstructor().newInstance()).build(),
            this.executorProperty.getRejectedExecutionHandler().getConstructor().newInstance());
    }

    @Bean(BeanNameConstant.BUILTIN_THREADUTIL)
    ThreadUtil threadUtil() throws Exception {
        if (executorProperty.getThreadUtilProvider() == null) {
            return new SimpleThreadUtil();
        } else {
            return executorProperty.getThreadUtilProvider().getConstructor().newInstance();
        }
    }

}
