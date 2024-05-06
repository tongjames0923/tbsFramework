package tbs.framework.cache.impls.broker;

import tbs.framework.cache.ICacheBroker;
import tbs.framework.cache.ICacheService;

public class NoneNullCacheBroker implements ICacheBroker {

    @Override
    public void setCache(String key, ICacheService cacheService, Object value, int[] intargs, String[] strargs) {
        if (value != null) {
            cacheService.put(key, value, true);
        }
    }
}
