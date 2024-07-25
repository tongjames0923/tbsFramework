package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.base.model.AsyncReceipt;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public abstract class ThreadUtil {
    public static interface IReceiptBroker {
        /**
         * 提交回执，在完成任务的第一时间发生
         *
         * @param receipt 回执数据
         */
        void submitReceipt(AsyncReceipt receipt);

        /**
         * 确认消息
         *
         * @param receiptId
         */
        void acknowledgeReceipt(String receiptId);
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
