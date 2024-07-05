package tbs.framework.cache;

public interface ICacheAspectJudgeMaker {
    AbstractCacheEliminationStrategy.ICacheEliminationJudge<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> makeJudge(
        String key, int[] intArgs, String[] strArgs);
}
