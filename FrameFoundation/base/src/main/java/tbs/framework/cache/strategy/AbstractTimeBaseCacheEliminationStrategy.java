package tbs.framework.cache.strategy;

import tbs.framework.cache.hooks.ITimeBaseSupportedHook;
import tbs.framework.cache.managers.AbstractTimeBaseCacheManager;

/**
 * @author Abstergo
 */
public abstract class AbstractTimeBaseCacheEliminationStrategy
    extends AbstractCacheEliminationStrategy<ITimeBaseSupportedHook, AbstractTimeBaseCacheManager> {
}
