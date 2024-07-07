package tbs.framework.redis.impls.cache.hooks;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.hooks.ITimeBaseSupportedHook;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager;
import tbs.framework.redis.impls.cache.services.RedisCacheServiceImpl;
import tbs.framework.utils.BeanUtil;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class SimpleRedisHook implements ITimeBaseSupportedHook {

    public RedisCacheServiceImpl getService(ICacheService cacheService) {
        return BeanUtil.getAs(cacheService);
    }

    @Override
    public void onSetDelay(@NotNull String key, @NotNull Duration delay,
        @NotNull AbstractTimeBaseCacheManager service) {

        RedisCacheServiceImpl cacheService = getService(service.getCacheService());
        cacheService.getRedisTemplate().expire(cacheService.mixKey(key), delay);
    }

    @Override
    public void onTimeout(@NotNull String key, @NotNull ICacheService service) {

    }

    @Override
    public long remainingTime(@NotNull String key, @NotNull AbstractTimeBaseCacheManager service) {
        RedisCacheServiceImpl cacheService = getService(service.getCacheService());
        return cacheService.getRedisTemplate().getExpire(cacheService.mixKey(key), TimeUnit.SECONDS);
    }

    @Override
    public void onSetCache(@NotNull String key, Object value, boolean override, @NotNull AbstractCacheManager host) {

    }

    @Override
    public Object onGetCache(String key, @NotNull AbstractCacheManager cacheService, Object value) {
        return value;
    }

    @Override
    public void onRemoveCache(String key, @NotNull AbstractCacheManager cacheService) {

    }

    @Override
    public void onClearCache(@NotNull AbstractCacheManager cacheService) {

    }

    @Override
    public void onTestCache(String key, @NotNull AbstractCacheManager cacheService) {

    }

    @Override
    public boolean hookAvaliable(int type, @NotNull AbstractCacheManager host) {
        return ITimeBaseSupportedHook.super.hookAvaliable(type, host) && host.getCacheService() instanceof RedisCacheServiceImpl;
    }
}
