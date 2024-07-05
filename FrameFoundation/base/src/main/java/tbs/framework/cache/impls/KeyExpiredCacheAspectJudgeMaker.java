package tbs.framework.cache.impls;

import tbs.framework.cache.AbstractCacheEliminationStrategy;
import tbs.framework.cache.AbstractTimeBaseCacheManager;
import tbs.framework.cache.ICacheAspectJudgeMaker;
import tbs.framework.cache.ITimeBaseSupportedHook;
import tbs.framework.cache.impls.eliminate.judges.SingleKeyTestExpiredCacheJudge;

/**
 * 针对单Key的超时清除缓存评判生成器
 *
 * @author Abstergo
 */
public class KeyExpiredCacheAspectJudgeMaker implements ICacheAspectJudgeMaker {
    @Override
    public AbstractCacheEliminationStrategy.ICacheEliminationJudge<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> makeJudge(
        String key, int[] intArgs, String[] strArgs) {
        AbstractCacheEliminationStrategy.ICacheEliminationJudge<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager>
            judge = new SingleKeyTestExpiredCacheJudge(key, intArgs != null && intArgs.length > 0 ? intArgs[0] : -1);
        return judge;
    }
}
