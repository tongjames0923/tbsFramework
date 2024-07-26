package tbs.framework.utils

import cn.hutool.extra.spring.SpringUtil
import org.springframework.util.ErrorHandler
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

/**
 * 用于构建和执行线程任务的工具类。
 */
class ThreadUtilRunnableTaskBuilder {
    private var mTasks: MutableList<Runnable> = LinkedList<Runnable>()
    private var isBlock: Boolean = false

    private var executorService: ExecutorService? = null

    private var errorHandle: ErrorHandler? = null;

    /**
     * 构造函数，初始化任务列表。
     *
     * @param runnables 可变参数，包含要执行的任务。
     */
    public constructor(vararg runnables: Runnable) {
        mTasks.addAll(runnables)
    }

    public constructor(runables: Collection<Runnable>) {
        mTasks.addAll(runables)
    }

    /**
     * 设置特定的ExecutorService。
     *
     * @param executorService ExecutorService对象，用于执行任务。
     * @return 当前对象，用于链式调用。
     */
    public fun specialExecutorService(executorService: ExecutorService): ThreadUtilRunnableTaskBuilder {
        this.executorService = executorService
        return this
    }

    public fun errorHandle(errorHandle: ErrorHandler): ThreadUtilRunnableTaskBuilder {
        this.errorHandle = errorHandle
        return this
    }

    /**
     * 内部方法，用于在CountDownLatch上减少计数。
     *
     * @param countDownLatch CountDownLatch对象，如果为null则不执行任何操作。
     */
    private fun doneOnce(countDownLatch: CountDownLatch?) {
        if (countDownLatch == null) return
        countDownLatch.countDown()
    }

    /**
     * 设置任务以异步方式执行。
     *
     * @return 当前对象，用于链式调用。
     */
    public fun runWithAsync(): ThreadUtilRunnableTaskBuilder {
        isBlock = false
        return this
    }

    /**
     * 设置任务以同步方式执行。
     *
     * @return 当前对象，用于链式调用。
     */
    public fun runWithSynchronous(): ThreadUtilRunnableTaskBuilder {
        isBlock = true
        return this
    }

    /**
     * 执行任务列表。
     */
    public fun execute() {
        val ls = mTasks;
        val es = if (executorService == null) SpringUtil.getBean(ExecutorService::class.java) else executorService!!
        var latch: CountDownLatch? = null
        if (isBlock) {
            latch = CountDownLatch(ls.size)
        }
        ls.forEach {
            es.execute {
                try {
                    it.run()
                } catch (e: Exception) {
                    errorHandle?.handleError(e);
                } finally {
                    doneOnce(latch)
                }
            }
        }
        latch?.await()
    }
}
