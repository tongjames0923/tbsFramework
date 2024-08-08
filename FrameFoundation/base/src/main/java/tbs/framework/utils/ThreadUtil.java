package tbs.framework.utils;

import tbs.framework.base.model.AsyncReceipt;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 线程工具类
 *
 * <p>该类提供了一系列用于处理线程和任务的静态方法。通过此类，可以方便地执行异步操作、批量任务等。
 *
 * @author abstergo
 */
public abstract class ThreadUtil {

    /**
     * 回执代理接口
     */
    public static interface IReceiptBroker {
        /**
         * 提交回执
         *
         * <p>在完成任务的第一时间提交回执数据。
         *
         * @param receipt 回执数据
         */
        void submitReceipt(AsyncReceipt receipt);

        /**
         * 确认消息
         *
         * <p>确认消息已接收。
         *
         * @param receiptId 回执ID
         */
        void acknowledgeReceipt(String receiptId);
    }

    /**
     * 线程工具实例
     */
    private static ThreadUtil threadUtil;

    /**
     * 获取线程工具实例
     *
     * <p>该方法用于获取线程工具类的唯一实例。
     *
     * @return 线程工具实例
     */
    public static ThreadUtil getInstance() {
        return SingletonHolder.getInstance(ThreadUtil.class);
    }

    /**
     * 获取执行器
     *
     * <p>该方法用于获取线程池执行器。
     *
     * @return 执行器
     */
    protected abstract ExecutorService getExecutorService();

    /**
     * 运行任务
     *
     * <p>该方法用于在后台运行一个或多个任务。
     *
     * @param runnables 要运行的任务
     */
    public void runCollectionInBackground(Runnable... runnables) {
        new ThreadUtilRunnableTaskBuilder(runnables).specialExecutorService(getExecutorService()).runWithAsync()
            .execute();
    }

    /**
     * 批量运行任务
     *
     * <p>该方法用于在后台批量运行任务。
     *
     * @param runnables 要运行的任务集合
     */
    public void runCollectionInBackground(Collection<Runnable> runnables) {
        new ThreadUtilRunnableTaskBuilder(runnables).runWithAsync().specialExecutorService(getExecutorService())
            .execute();
    }

    /**
     * 运行任务
     *
     * <p>该方法用于运行一个或多个任务。
     *
     * @param conf 任务配置
     */
    public void runWithRunableTask(ThreadUtilRunnableTaskBuilder conf) {
        if (conf == null) {
            return;
        }
        conf.execute();
    }

    /**
     * 运行异步任务
     *
     * <p>该方法用于运行异步任务并返回结果。
     *
     * @param tasks   要运行的任务列表
     * @param timeout 任务执行超时时间
     * @param unit    超时时间单位
     * @param <T>     任务返回结果类型
     * @return 任务结果列表
     * @throws InterruptedException 中断异常
     */
    public <T> List<Future<T>> callAndAwait(List<Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException {
        return getExecutorService().invokeAll(tasks, timeout, unit);
    }

}
