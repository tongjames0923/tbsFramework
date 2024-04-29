package tbs.framework.timer;

import tbs.framework.base.utils.UuidUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>Abstract AbstractTimer class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public abstract class AbstractTimer {
    public interface ITimerCallback {
        /**
         * 回调
         */
        void callback();

        /**
         * 出现异常回调
         *
         * @param e
         */
        void onException(Exception e);
    }

    /**
     * 延迟调用
     *
     * @param callback 调用方法
     * @param delay    延迟时间
     * @param timeUnit 时间单位
     */
    public void scheduled(final ITimerCallback callback, final long delay, final TimeUnit timeUnit) {
        final String session = UuidUtils.getUuid();
        if (!this.before(session, callback, delay, timeUnit)) {
            return;
        }
        try {
            this.protectedScheduled(session, callback, delay, timeUnit);
        } catch (final Exception e) {
            callback.onException(e);
        }
        this.after(session, callback, delay, timeUnit);
    }

    /**
     * <p>scheduledAt.</p>
     *
     * @param callback a {@link tbs.framework.timer.AbstractTimer.ITimerCallback} object
     * @param at a {@link java.time.LocalDateTime} object
     */
    public void scheduledAt(final ITimerCallback callback, final LocalDateTime at) {
        final LocalDateTime now = LocalDateTime.now();
        final Duration duration = Duration.between(now, at);
        this.scheduled(callback, duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 准备计时前
     *
     * @param callback 计时回调
     * @param delay    延迟时间
     * @param timeUnit 时间单位
     * @param uid      运行会话id
     * @return true 启动计时 false拒绝计时
     */
    protected boolean before(final String uid, final ITimerCallback callback, final long delay, final TimeUnit timeUnit) {
        return true;
    }

    /**
     * 成功计时后
     *
     * @param uid      运行会话id
     * @param callback 计时回调
     * @param delay    延迟时间
     * @param timeUnit 时间单位
     */
    protected void after(final String uid, final ITimerCallback callback, final long delay, final TimeUnit timeUnit) {
    }

    /**
     * 延迟调用的内部实现
     *
     * @param uid a {@link java.lang.String} object
     * @param callback 调用方法
     * @param delay    延迟时间
     * @param timeUnit 延迟单位
     */
    protected abstract void protectedScheduled(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit);
}
