package tbs.framework.base.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.properties.ExecutorProperty;
import tbs.framework.base.utils.LogUtil;

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

    private ILogger logger;

    AsyncConfig(LogUtil util) {
        logger = util.getLogger(AsyncConfig.class.getName());
        logger.info("Async config initialized");
    }


    @Bean(name = BeanNameConstant.ASYNC_EXECUTOR)
    public ExecutorService asyncExecutor()
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return new ThreadPoolExecutor(executorProperty.getCorePoolSize(), executorProperty.getMaxPoolSize(),
            executorProperty.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingQueue<>(32),
            new ThreadFactoryBuilder().setNamePrefix("main-pool-").build(),
            executorProperty.getRejectedExecutionHandler().getConstructor().newInstance());
    }

}
