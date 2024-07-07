package tbs.framework.cache;

import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy;

public interface ICacheAspectJudgeMaker {
    AbstractCacheEliminationStrategy.ICacheEliminationJudge makeJudge(
        String key, int[] intArgs, String[] strArgs);
}
