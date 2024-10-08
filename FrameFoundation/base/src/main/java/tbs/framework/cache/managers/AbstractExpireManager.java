package tbs.framework.cache.managers;

import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.hooks.IExpireSupportedHook;
import tbs.framework.cache.supports.IExpireSupport;

import java.time.Duration;

/**
 * 支持超时的缓存管理器
 *
 * @author Abstergo
 */
public abstract class AbstractExpireManager extends AbstractCacheManager implements IExpireSupport {

    /**
     * 获取可超时器以支持超时功能
     *
     * @return the expireable
     */
    protected abstract IExpireable getExpireable();

    @Override
    public void expire(String key, Duration time) {
        hookForExpire(key, time);
        ICacheService cacheService = getCacheService();
        getExpireSupportOrThrows(cacheService).expire(key, time, this, getCacheService());
    }

    protected IExpireable getExpireSupportOrThrows(ICacheService cacheService) {
        return getExpireable().isAccept(this, cacheService)
            .orElseThrow(() -> new IllegalArgumentException("expire function is not support inused cache service"));
    }

    /**
     * 设置超时的钩子运行实现
     *
     * @param key  the key
     * @param time the time
     */
    protected void hookForExpire(String key, Duration time) {
        foreachHook((h) -> {
            ((IExpireSupportedHook)h).onSetDelay(key, time, this);
        }, IExpireSupportedHook.HOOK_OPERATE_FLAG_EXPIRE);
    }

    @Override
    public Duration remaining(String key) {
        ICacheService cacheService = getCacheService();
        return Duration.ofMillis(getExpireSupportOrThrows(cacheService).remaining(key, this, cacheService));
    }

    public Object getAndRemove(String key, Duration delay) {
        if (delay.toMillis() <= 0) {
            return get(key);
        } else {
            Object value = get(key);
            expire(key, delay);
            return value;
        }
    }

    public void putAndRemove(String key, Object value, boolean ov, Duration delay) {
        if (delay.toMillis() <= 0) {
            put(key, value, ov);
        } else {
            put(key, value, ov);
            expire(key, delay);
        }
    }

}
