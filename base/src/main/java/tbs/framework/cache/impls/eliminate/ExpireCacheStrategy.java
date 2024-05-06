package tbs.framework.cache.impls.eliminate;

import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IEliminationStrategy;

public class ExpireCacheStrategy implements IEliminationStrategy {

    @Override
    public void eliminate(String finalKey, ICacheService cacheService, String[] strArgs, int[] intArgs) {
        if (intArgs.length >= 1) {
            cacheService.expire(finalKey, intArgs[0]);
        }
    }
}
