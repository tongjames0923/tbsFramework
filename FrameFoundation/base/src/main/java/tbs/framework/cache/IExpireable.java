package tbs.framework.cache;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.managers.AbstractCacheManager;

import java.time.Duration;

/**
 * 缓存超时功能接口
 */
public interface IExpireable {
    /**
     * Expire.
     *
     * @param key          the key
     * @param duration     the duration
     * @param manager      the manager
     * @param cacheService the cache service
     */
    public void expire(@NotNull String key, @NotNull Duration duration, @NotNull AbstractCacheManager manager,
        @NotNull ICacheService cacheService);

    /**
     * Remaining long.
     *
     * @param key          the key
     * @param manager      the manager
     * @param cacheService the cache service
     * @return the long
     */
    public long remaining(@NotNull String key, @NotNull AbstractCacheManager manager,
        @NotNull ICacheService cacheService);

    /**
     * Execute.
     */
    public void execute();
}
