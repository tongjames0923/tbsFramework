package tbs.framework.timer;

import tbs.framework.base.utils.UuidUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
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
    public void scheduled(ITimerCallback callback, long delay, TimeUnit timeUnit) {
        String session = UuidUtils.getUuid();
        if (!before(session, callback, delay, timeUnit)) {
            return;
        }
        try {
            protectedScheduled(session, callback, delay, timeUnit);
        } catch (Exception e) {
            callback.onException(e);
        }
        after(session, callback, delay, timeUnit);
    }

    public void scheduledAt(ITimerCallback callback, LocalDateTime at) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, at);
        scheduled(callback, duration.toMillis(), TimeUnit.MILLISECONDS);
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
    protected boolean before(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit) {
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
    protected void after(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit) {
    }

    /**
     * 延迟调用的内部实现
     *
     * @param uid
     * @param callback 调用方法
     * @param delay    延迟时间
     * @param timeUnit 延迟单位
     */

    protected abstract void protectedScheduled(String uid, ITimerCallback callback, long delay, TimeUnit timeUnit);
}
