package tbs.framework.redis.impls.cache.managers;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.cache.impls.hooks.LocalTimeoutEliminateHook;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.managers.AbstractTimebaseHybridCacheManager;
import tbs.framework.redis.impls.cache.hooks.SimpleRedisHook;
import tbs.framework.redis.impls.cache.services.RedisCacheServiceImpl;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author abstergo
 */
public class HybridCacheManager extends AbstractTimebaseHybridCacheManager {

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addService(SpringUtil.getBean(ConcurrentMapCacheServiceImpl.class));
        this.addService(SpringUtil.getBean(RedisCacheServiceImpl.class));
        this.addHook(new LocalTimeoutEliminateHook());
        this.addHook(new SimpleRedisHook());
        setService(0);
    }

    public Map<String, Set<Integer>> getKeysExistInServiceIndex(String... keys) {
        Map<String, Set<Integer>> result = new HashMap<>();
        for (String l : keys) {
            Set<Integer> set = new HashSet<>();
            selectService((c, i) -> {
                if (c.exists(l)) {
                    set.add(i);
                }
                return false;
            });
            result.put(l, set);
        }
        return result;
    }

    @Override
    public void clear() {
        selectService((c, i) -> {
            synchronized (c) {
                super.clear();
                setService(i);
            }
            return false;
        });
    }

    @Override
    public long size() {
        Long[] r = new Long[] {0L};
        selectService((c, i) -> {
            r[0] += c.cacheSize();
            return false;
        });
        return r[0];
    }

    @Override
    public void put(String key, Object value, boolean override) {
        setService(0);
        super.put(key, value, override);
    }

    @Override
    public Object get(String key) {
        int k = selectService((c, i) -> {
            return c.exists(key);
        });
        if (k < serviceCount()) {
            setService(k);
        }
        return super.get(key);
    }

    @Override
    public boolean exists(String key) {

        int k = selectService((c, i) -> {
            return c.exists(key);
        });
        if (k >= serviceCount()) {
            return false;
        }
        return super.exists(key);
    }

    @Override
    public void remove(String key) {
        selectService((c, i) -> {
            synchronized (c) {
                setService(i);
                super.remove(key);
            }
            return false;
        });

    }

    @Override
    public void expire(String key, Duration time) {
        selectService((c, i) -> {
            synchronized (c) {
                setService(i);
                super.expire(key, time);
            }
            return false;
        });
    }

    @Override
    public Duration remaining(String key) {
        Duration[] d = new Duration[] {null};
        int k = selectService((c, i) -> {
            synchronized (c) {
                setService(i);
                d[0] = super.remaining(key);
            }
            return d[0] != null;
        });

        return d[0];
    }
}
