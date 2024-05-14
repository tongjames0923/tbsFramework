package tbs.framework.utils;

import cn.hutool.extra.spring.SpringUtil;

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
    private static ThreadUtil threadUtil;

    public static ThreadUtil getInstance() {
        if (threadUtil == null) {
            threadUtil = SpringUtil.getBean(ThreadUtil.class);
        }
        return threadUtil;
    }

    /**
     * 获取执行器
     *
     * @return
     */
    protected abstract ExecutorService getExecutorService();

    public void runCollectionInBackground(Runnable... runnables) {
        runCollectionInBackground(Arrays.asList(runnables));
    }

    public void runCollectionInBackground(Collection<Runnable> runnables) {
        for (Runnable r : runnables) {
            getExecutorService().execute(r);
        }
    }

    public <T> List<Future<T>> callAndAwait(List<Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        return getExecutorService().invokeAll(tasks, timeout, unit);
    }
}
