package tbs.framework.timer.impls;

import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.timer.AbstractTimer;
import tbs.framework.utils.LogFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 使用内置ScheduledExecutorService实现的计时器
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class ScheduledExecutorTimer extends AbstractTimer {

    private final ScheduledExecutorService scheduledExecutorService;

    @AutoLogger
    private ILogger logger;

    /**
     * <p>Constructor for ScheduledExecutorTimer.</p>
     *
     * @param scheduledExecutorService a {@link java.util.concurrent.ScheduledExecutorService} object
     * @param logUtil a {@link LogFactory} object
     */
    public ScheduledExecutorTimer(final ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    protected void protectedScheduled(final String uid, final ITimerCallback callback, final long delay, final TimeUnit timeUnit) {
        this.scheduledExecutorService.schedule(callback::callback, delay, timeUnit);
    }

    @Override
    protected boolean before(final String uid, final ITimerCallback callback, final long delay, final TimeUnit timeUnit) {
        this.logger.trace(String.format("%s will Scheduled at %d %s", uid, delay, timeUnit.toString()));

        return true;
    }

    @Override
    protected void after(final String uid, final ITimerCallback callback, final long delay, final TimeUnit timeUnit) {
        this.logger.trace(String.format("%s Scheduled", uid));
    }
}
