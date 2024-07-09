package tbs.framework.cache.impls.eliminations.strategys

import tbs.framework.cache.managers.AbstractCacheManager
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy.ICacheEliminationBroker
import tbs.framework.cache.supports.IExpireSupport
import java.time.Duration

/**
 * 基础超时清除缓存逻辑
 *
 */
class ExpiredCacheElimination : AbstractCacheEliminationStrategy(),
    ICacheEliminationBroker {
    override fun getEliminationBroker(): ICacheEliminationBroker {
        return this
    }

    override fun eliminated(
        cacheManager: AbstractCacheManager,
        judge: ICacheEliminationJudge
    ): Boolean {
        for (i in judge.killList()) {
            if (cacheManager is IExpireSupport) {
                cacheManager.expire(i, Duration.ofMillis(judge.paramter(0)));
            }
        }
        return true
    }
}