import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import tbs.framework.cache.ICacheService;

public class CaffeineCacheService implements ICacheService {
    private final Cache<String, Object> cache;

    public CaffeineCacheService() {
        // 初始化 Caffeine 缓存，这里设置了最大缓存项为 100，并且每项数据5分钟后过期
        this.cache = Caffeine.newBuilder().build();
    }

    public Cache<String, Object> getSource() {
        return cache;
    }

    @Override
    public void put(String key, Object value, boolean override) {
        if (override || !exists(key)) {
            cache.put(key, value);
        }
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public boolean exists(String key) {
        return cache.asMap().containsKey(key);
    }

    @Override
    public void remove(String key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public long cacheSize() {
        return cache.estimatedSize();
    }

}