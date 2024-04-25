package tbs.framework.timer.impls;

import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.timer.AbstractTimer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 使用内置ScheduledExecutorService实现的计时器
 */
public class ScheduledExecutorTimer extends AbstractTimer {

    private final ScheduledExecutorService scheduledExecutorService;

    private final ILogger logger;

    public ScheduledExecutorTimer(ScheduledExecutorService scheduledExecutorService, LogUtil logUtil) {
        this.scheduledExecutorService = scheduledExecutorService;
        logger = logUtil.getLogger(ScheduledExecutorTimer.class.getName());
    }

    @Override
    protected void protectedScheduled(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit) {
        scheduledExecutorService.schedule(callback::callback, delay, timeUnit);
    }

    @Override
    protected boolean before(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit) {
        logger.trace(String.format("%s will Scheduled at %d %s", uid, delay, timeUnit.toString()));

        return true;
    }

    @Override
    protected void after(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit) {
        logger.trace(String.format("%s Scheduled", uid));
    }
}
