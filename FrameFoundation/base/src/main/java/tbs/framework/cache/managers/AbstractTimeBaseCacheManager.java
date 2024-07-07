package tbs.framework.cache.managers;

import tbs.framework.cache.IExpireable;
import tbs.framework.cache.constants.FeatureSupportCode;
import tbs.framework.cache.hooks.ITimeBaseSupportedHook;
import tbs.framework.cache.supports.ITimeBaseCacheSupport;

import java.time.Duration;

/**
 * 支持超时的缓存管理器
 *
 * @author Abstergo
 */
public abstract class AbstractTimeBaseCacheManager extends AbstractCacheManager implements ITimeBaseCacheSupport {

    @Override
    public boolean featureSupport(int code) {
        return code == FeatureSupportCode.EXPIRED_SUPPORT || super.featureSupport(code);
    }

    /**
     * 获取可超时器以支持超时功能
     *
     * @return the expireable
     */
    protected abstract IExpireable getExpireable();

    @Override
    public void expire(String key, Duration time) {
        hookForExpire(key, time);
        getExpireable().expire(key, time, this, getCacheService());
    }

    /**
     * 设置超时的钩子运行实现
     *
     * @param key  the key
     * @param time the time
     */
    protected void hookForExpire(String key, Duration time) {
        foreachHook((h) -> {
            ((ITimeBaseSupportedHook)h).onSetDelay(key, time, this);
        }, ITimeBaseSupportedHook.HOOK_OPERATE_FLAG_EXPIRE);
    }

    @Override
    public Duration remaining(String key) {
        return Duration.ofMillis(getExpireable().remaining(key, this, getCacheService()));
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
