package tbs.framework.cache;

public interface ICacheBroker {
    void setCache(String key,ICacheService cacheService, Object value, int[] intargs, String[] strargs);
}
