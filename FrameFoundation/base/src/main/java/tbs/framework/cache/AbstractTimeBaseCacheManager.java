package tbs.framework.cache;

import java.time.Duration;

/**
 * @author Abstergo
 */
public abstract class AbstractTimeBaseCacheManager extends AbstractCacheManager<ITimeBaseSupportedHook>
    implements ITimeBaseCacheSupport {

    @Override
    public void addHook(ITimeBaseSupportedHook hook) {
        if (hookCount() > 0) {
            throw new RuntimeException("only one hook is allowed");
        }
        super.addHook(hook);
    }

    @Override
    public void expire(String key, Duration time) {
        if (hookCount() < 1) {
            throw new RuntimeException("no hook found,can not support this function");
        }
        foreachHook((h) -> {
            h.onSetDelay(key, time, getCacheService());
        });
    }

    @Override
    public Duration remaining(String key) {
        if (hookCount() < 1) {
            throw new RuntimeException("no hook found,can not support this function");
        }
        final Duration[] d = new Duration[1];
        foreachHook((h) -> {
            d[0] = Duration.ofMillis(h.remainingTime(key, getCacheService()));
        });
        return d[0];
    }

    @Override
    public Object getAndRemove(String key, Duration delay) {
        if (hookCount() < 1) {
            throw new RuntimeException("no hook found,can not support this function");
        }
        if (delay.toMillis() <= 0) {
            return get(key);
        } else {
            Object value = get(key);
            expire(key, delay);
            return value;
        }
    }

}
