package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.base.model.AsyncReceipt;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public abstract class ThreadUtil {
    public static interface IReceiptConsumer {
        /**
         * 消费回执
         *
         * @param receipt 回执数据
         */
        void consumeReceipt(AsyncReceipt receipt);
    }

    /**
     *
     */
    private static ThreadUtil threadUtil;

    /**
     *
     */
    public static ThreadUtil getInstance() {
        if (threadUtil == null) {
            threadUtil = SpringUtil.getBean(ThreadUtil.class);
        }
        return threadUtil;
    }

    /**
     * 获取执行器
     */
    protected abstract ExecutorService getExecutorService();

    /**
     * @see #runCollectionInBackground(Collection)
     */
    public void runCollectionInBackground(Runnable... runnables) {
        runCollectionInBackground(Arrays.asList(runnables));
    }

    /**
     * 异步批量运行
     */
    public void runCollectionInBackground(Collection<Runnable> runnables) {
        for (Runnable r : runnables) {
            getExecutorService().execute(r);
        }
    }

    /**
     * 运行异步future
     */
    public <T> List<Future<T>> callAndAwait(List<Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        return getExecutorService().invokeAll(tasks, timeout, unit);
    }
}
