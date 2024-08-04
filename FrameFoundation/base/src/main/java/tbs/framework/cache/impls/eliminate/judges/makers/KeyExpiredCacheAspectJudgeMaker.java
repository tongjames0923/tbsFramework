package tbs.framework.cache.impls.eliminate.judges.makers;

import tbs.framework.cache.ICacheAspectJudgeMaker;
import tbs.framework.cache.impls.eliminate.judges.SingleKeyTestExpiredCacheJudge;
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy;

/**
 * 针对单Key的超时清除缓存评判生成器，从intargs中的第一项获取超时时间
 *
 * @author Abstergo
 */
public class KeyExpiredCacheAspectJudgeMaker implements ICacheAspectJudgeMaker {
    @Override
    public AbstractCacheEliminationStrategy.ICacheEliminationJudge makeJudge(String key, int[] intArgs,
        String[] strArgs) {
        AbstractCacheEliminationStrategy.ICacheEliminationJudge judge =
            new SingleKeyTestExpiredCacheJudge(key, intArgs != null && intArgs.length > 0 ? intArgs[0] : -1);
        return judge;
    }
}
