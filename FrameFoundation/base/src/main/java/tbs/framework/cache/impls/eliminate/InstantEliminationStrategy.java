package tbs.framework.cache.impls.eliminate;

import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IEliminationStrategy;

public class InstantEliminationStrategy implements IEliminationStrategy {

    @Override
    public void eliminate(String finalKey, ICacheService cacheService, String[] strArgs, int[] intArgs) {
        cacheService.remove(finalKey);
    }
}
