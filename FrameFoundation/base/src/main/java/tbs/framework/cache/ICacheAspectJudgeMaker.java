package tbs.framework.cache;

import tbs.framework.cache.hooks.ITimeBaseSupportedHook;
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager;
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy;

public interface ICacheAspectJudgeMaker {
    AbstractCacheEliminationStrategy.ICacheEliminationJudge<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> makeJudge(
        String key, int[] intArgs, String[] strArgs);
}
