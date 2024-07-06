package tbs.framework.cache.impls.eliminate.judges;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy;
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager;
import tbs.framework.cache.hooks.ITimeBaseSupportedHook;

import java.util.Set;

/**
 * 单键缓存超时判别器
 *
 * @author Abstergo
 */
public class SingleKeyTestExpiredCacheJudge implements
    AbstractCacheEliminationStrategy.ICacheEliminationJudge<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> {

    private String key;
    private long expired;

    public SingleKeyTestExpiredCacheJudge(final String key, final long expired) {
        this.key = key;
        this.expired = expired;
    }

    @Override
    public boolean isEliminated(@NotNull AbstractTimeBaseCacheManager cacheManager) {
        return cacheManager.exists(this.key);
    }

    @Override
    public @NotNull Set<String> killList() {
        return Set.of(key);
    }

    @Override
    public <T> T paramter(int k) {
        return (T)Long.valueOf(expired);
    }
}
