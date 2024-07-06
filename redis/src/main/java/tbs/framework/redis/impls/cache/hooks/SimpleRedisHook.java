package tbs.framework.redis.impls.cache.hooks;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.hooks.ITimeBaseSupportedHook;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.redis.impls.cache.services.RedisCacheServiceImpl;
import tbs.framework.utils.BeanUtil;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SimpleRedisHook implements ITimeBaseSupportedHook {

    public RedisCacheServiceImpl getService(ICacheService cacheService) {
        return BeanUtil.getAs(cacheService);
    }

    @Override
    public void onSetDelay(String key, Duration delay, ICacheService service) {

        RedisCacheServiceImpl cacheService = getService(service);
        cacheService.getRedisTemplate().expire(cacheService.mixKey(key), delay);
    }

    @Override
    public void onTimeout(String key, ICacheService service) {

    }

    @Override
    public long remainingTime(String key, ICacheService service) {
        RedisCacheServiceImpl cacheService = getService(service);
        return cacheService.getRedisTemplate().getExpire(cacheService.mixKey(key), TimeUnit.SECONDS);
    }

    @Override
    public void onSetCache(@NotNull String key, Object value, boolean override, ICacheService cacheService) {

    }

    @Override
    public Object onGetCache(String key, ICacheService cacheService, Object value) {
        return value;
    }

    @Override
    public void onRemoveCache(String key, ICacheService cacheService) {

    }

    @Override
    public void onClearCache(ICacheService cacheService) {

    }

    @Override
    public void onTestCache(String key, ICacheService cacheService) {

    }

    @Override
    public boolean hookAvaliable(int type, @NotNull AbstractCacheManager host) {
        return ITimeBaseSupportedHook.super.hookAvaliable(type, host) &&
            host.getCacheService() instanceof RedisCacheServiceImpl;
    }
}
