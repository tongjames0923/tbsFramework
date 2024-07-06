package tbs.framework.cache.impls.eliminations.strategys

import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy.ICacheEliminationBroker
import tbs.framework.cache.strategy.AbstractTimeBaseCacheEliminationStrategy
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager
import tbs.framework.cache.hooks.ITimeBaseSupportedHook
import java.time.Duration

/**
 * 基础超时清除缓存逻辑
 *
 */
class ExpiredCacheElimination : AbstractTimeBaseCacheEliminationStrategy(),
    ICacheEliminationBroker<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> {
    override fun getEliminationBroker(): ICacheEliminationBroker<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> {
        return this
    }

    override fun eliminated(
        cacheManager: AbstractTimeBaseCacheManager,
        judge: ICacheEliminationJudge<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager>
    ): Boolean {
        for (i in judge.killList()) {
            cacheManager.expire(i, Duration.ofMillis(judge.paramter(0)));
        }
        return true
    }
}