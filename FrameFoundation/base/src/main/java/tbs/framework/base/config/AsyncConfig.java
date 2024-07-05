package tbs.framework.base.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import tbs.framework.async.task.aspects.AsyncTaskAop;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.interfaces.impls.threads.handlers.LogExceptionHandler;
import tbs.framework.base.properties.AsyncTaskProperty;
import tbs.framework.base.properties.ExecutorProperty;
import tbs.framework.utils.BeanUtil;
import tbs.framework.utils.ThreadUtil;
import tbs.framework.utils.impls.SimpleThreadUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

/**
 * @author abstergo
 */
public class AsyncConfig {
    @Resource
    ExecutorProperty executorProperty;

    @Resource
    AsyncTaskProperty asyncTaskProperty;

    @Bean
    AsyncTaskAop asyncTaskAop() {
        return new AsyncTaskAop();
    }

    @Bean(BeanNameConstant.BUILTIN_ASYNC_TASK_CALLBACK)
    ThreadUtil.IReceiptBroker receiptConsumer() throws Exception {
        return asyncTaskProperty.getReceiptBroker().getConstructor().newInstance();
    }

    @Bean(BeanNameConstant.ASYNC_EXECUTOR_EXCEPTION_HANDLER)
    Thread.UncaughtExceptionHandler executorServiceUncaughtExceptionHandler() throws Exception {
        return BeanUtil.buildBeanFromProperties(new LogExceptionHandler(),
            executorProperty.getUncaughtExceptionHandler(), BeanUtil::useEmptyArgs);
    }

    @Bean(name = BeanNameConstant.ASYNC_EXECUTOR_REJECT_HANDLER)
    RejectedExecutionHandler executorServiceRejectHandler() throws Exception {
        return BeanUtil.buildBeanFromProperties(new ThreadPoolExecutor.CallerRunsPolicy(),
            executorProperty.getRejectedExecutionHandler(), BeanUtil::useEmptyArgs);
    }

    @Bean(name = BeanNameConstant.ASYNC_EXECUTOR)
    public ExecutorService asyncExecutor(
        @Qualifier(BeanNameConstant.ASYNC_EXECUTOR_EXCEPTION_HANDLER) Thread.UncaughtExceptionHandler exceptionHandler)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return new ThreadPoolExecutor(this.executorProperty.getCorePoolSize(), this.executorProperty.getMaxPoolSize(),
            this.executorProperty.getKeepAliveTime(), TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(this.executorProperty.getQueueCapacity()),
            new ThreadFactoryBuilder().setNamePrefix("main-pool-").setUncaughtExceptionHandler(exceptionHandler)
                .build(), this.executorProperty.getRejectedExecutionHandler().getConstructor().newInstance());
    }

    @Bean(BeanNameConstant.BUILTIN_THREADUTIL)
    ThreadUtil threadUtil() throws Exception {
        return BeanUtil.buildBeanFromProperties(new SimpleThreadUtil(), executorProperty.getThreadUtilProvider(),
            BeanUtil::useEmptyArgs);
    }

}
