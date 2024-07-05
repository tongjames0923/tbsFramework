package tbs.framework.cache.impls.eliminate;

import tbs.framework.cache.AbstractTimeBaseCacheManager;
import tbs.framework.cache.IEliminationStrategy;

/**
 * @author Abstergo
 */
public class SimpleFIFOEliminationStrategy implements IEliminationStrategy {


    @Override
    public void eliminate(String finalKey, AbstractTimeBaseCacheManager cacheService, String[] strArgs, int[] intArgs) {

    }
}
