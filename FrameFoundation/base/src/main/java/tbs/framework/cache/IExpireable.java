package tbs.framework.cache;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.managers.AbstractCacheManager;

import java.time.Duration;

/**
 * 缓存超时功能接口
 * @author Abstergo
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
     * Remaining long.单位:毫秒
     *
     * @param key          the key
     * @param manager      the manager
     * @param cacheService the cache service
     * @return the long
     */
    public long remaining(@NotNull String key, @NotNull AbstractCacheManager manager,
        @NotNull ICacheService cacheService);

    /**
     * 若有需要，执行此函数清理数据
     */
    public void execute();
}
