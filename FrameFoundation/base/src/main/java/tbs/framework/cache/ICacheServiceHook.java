package tbs.framework.cache;

/**
 * @author Abstergo
 */
public interface ICacheServiceHook {

    void onSetCache(String key, Object value, boolean override, ICacheService cacheService);

    void onGetCache(String key, ICacheService cacheService);

    void onRemoveCache(String key, ICacheService cacheService);

    void onClearCache(ICacheService cacheService);

    void onTestCache(String key, ICacheService cacheService);

    default boolean hookAvaliable() {
        return true;
    }
}
