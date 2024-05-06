package tbs.framework.cache.impls.broker;

import tbs.framework.cache.ICacheBroker;
import tbs.framework.cache.ICacheService;

public class NullableCacheBroker implements ICacheBroker {
    @Override
    public void setCache(String key, ICacheService cacheService, Object value, int[] intargs, String[] strargs) {
        cacheService.put(key, value, true);
    }
}
