package tbs.framework.cache;

import lombok.val;

import java.time.Duration;

/**
 * @author Abstergo
 */
public abstract class AbstractTimeBaseCacheManager extends AbstractCacheManager<ITimeBaseSupportedHook>
    implements ITimeBaseCacheSupport {

    @Override
    public void addHook(ITimeBaseSupportedHook hook) {
        super.addHook(hook);
    }

    @Override
    public void expire(String key, Duration time) {
        foreachHook((h) -> {
            h.onSetDelay(key, time, getCacheService());
        }, ITimeBaseSupportedHook.HOOK_OPERATE_FLAG_EXPIRE);
    }

    @Override
    public Duration remaining(String key) {
        final Duration[] d = new Duration[1];
        foreachHook((h) -> {
            val n = h.remainingTime(key, getCacheService());
            if (d[0] == null) {
                d[0] = Duration.ofMillis(n);
            } else {
                d[0] = d[0].toMillis() > n ? Duration.ofMillis(n) : d[0];
            }
        }, ITimeBaseSupportedHook.HOOK_OPERATE_FLAG_REMAIN);
        return d[0];
    }

    @Override
    public Object getAndRemove(String key, Duration delay) {
        if (delay.toMillis() <= 0) {
            return get(key);
        } else {
            Object value = get(key);
            expire(key, delay);
            return value;
        }
    }

}
