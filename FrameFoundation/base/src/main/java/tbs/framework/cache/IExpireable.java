package tbs.framework.cache;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.managers.AbstractCacheManager;

import java.time.Duration;

public interface IExpireable {
    public void expire(@NotNull String key, @NotNull Duration duration, @NotNull
        AbstractCacheManager manager, @NotNull ICacheService cacheService);

    public long remaining(@NotNull String key, @NotNull AbstractCacheManager manager,
        @NotNull ICacheService cacheService);

    public void execute();
}
