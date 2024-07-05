package tbs.framework.cache.impls.eliminate;

import tbs.framework.cache.AbstractTimeBaseCacheManager;
import tbs.framework.cache.IEliminationStrategy;

import java.time.Duration;

/**
 * @author Abstergo
 */
public class ExpireCacheStrategy implements IEliminationStrategy {

    @Override
    public void eliminate(String finalKey, AbstractTimeBaseCacheManager cacheService, String[] strArgs, int[] intArgs) {
        if (intArgs.length >= 1) {
            cacheService.expire(finalKey, Duration.ofSeconds(intArgs[0]));
        }
    }
}
