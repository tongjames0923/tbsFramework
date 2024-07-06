package tbs.framework.cache.managers;

import lombok.val;
import tbs.framework.cache.constants.FeatureSupportCode;
import tbs.framework.cache.hooks.ITimeBaseSupportedHook;
import tbs.framework.cache.supports.ITimeBaseCacheSupport;

import java.time.Duration;

/**
 * @author Abstergo
 */
public abstract class AbstractTimeBaseCacheManager extends AbstractCacheManager implements ITimeBaseCacheSupport {

    @Override
    public boolean featureSupport(int code) {
        return code == FeatureSupportCode.EXPIRED_SUPPORT || super.featureSupport(code);
    }

    @Override
    public void expire(String key, Duration time) {
        foreachHook((h) -> {
            ((ITimeBaseSupportedHook)h).onSetDelay(key, time, getCacheService());
        }, ITimeBaseSupportedHook.HOOK_OPERATE_FLAG_EXPIRE);
    }

    @Override
    public Duration remaining(String key) {
        final Duration[] d = new Duration[1];
        foreachHook((h) -> {
            val n = ((ITimeBaseSupportedHook)h).remainingTime(key, getCacheService());
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
