package tbs.framework.redis.impls.cache.managers;

import cn.hutool.extra.spring.SpringUtil;
import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IExpireable;
import tbs.framework.cache.constants.CacheServiceTypeCode;
import tbs.framework.cache.impls.LocalExpiredImpl;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.managers.AbstractTimebaseHybridCacheManager;
import tbs.framework.redis.impls.cache.RedisExpiredImpl;
import tbs.framework.redis.impls.cache.services.RedisCacheServiceImpl;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author abstergo
 */
public class HybridCacheManager extends AbstractTimebaseHybridCacheManager {

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addService(SpringUtil.getBean(ConcurrentMapCacheServiceImpl.class));
        this.addService(SpringUtil.getBean(RedisCacheServiceImpl.class));
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

            super.clear();
            setService(i);

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
    protected void putImpl(String key, Object value, boolean ov) {
        AtomicLong cacheSize = new AtomicLong(1);
        int s = selectService((c, i) -> {
            if (i == this.serviceCount() - 1) {
                c.put(key, value, ov);
                return true;
            } else {
                cacheSize.updateAndGet(v -> v * 8);
                if (c.cacheSize() > cacheSize.get()) {
                    return false;
                }
                c.put(key, value, ov);
                return true;
            }
        });
    }

    @Override
    protected Object getImpl(String key) {
        int k = selectService((c, i) -> {
            return c.exists(key);
        });
        if (k < serviceCount()) {
            Object[] r = new Object[] {null};
            operateCacheService(k, (s) -> {
                r[0] = s.get(key);
            });
            return r[0];
        }
        return null;
    }

    @Override
    protected boolean existsImpl(String key) {
        int k = selectService((c, i) -> {
            return c.exists(key);
        });
        if (k >= serviceCount()) {
            return false;
        }
        return true;
    }

    @Override
    protected void removeImpl(String key) {
        selectService((c, i) -> {
            c.remove(key);
            return false;
        });
    }

    @Override
    protected void clearImpl() {
        selectService((c, i) -> {
            operateCacheService(i, (ICacheService s) -> {
                s.clear();
            });
            return false;
        });
    }

    @Override
    protected IExpireable getExpireable() {
        return new IExpireable() {

            private LocalExpiredImpl localExpired = new LocalExpiredImpl();
            private RedisExpiredImpl redisExpired = new RedisExpiredImpl();

            private void expireByType(@NotNull String key, @NotNull Duration duration,
                @NotNull AbstractCacheManager manager, @NotNull ICacheService cacheService) {
                switch (cacheService.serviceType()) {
                    case CacheServiceTypeCode.LOCAL:
                        localExpired.expire(key, duration, manager, cacheService);
                        break;
                    case CacheServiceTypeCode.REDIS:
                        if (cacheService instanceof RedisCacheServiceImpl) {
                            redisExpired.expire(key, duration, manager, cacheService);
                            break;
                        }
                    default:
                        throw new UnsupportedOperationException("未知的服务类型");
                }
            }

            private long remainByType(@NotNull String key, @NotNull AbstractCacheManager manager,
                @NotNull ICacheService cacheService) {
                switch (cacheService.serviceType()) {
                    case CacheServiceTypeCode.LOCAL:
                        return localExpired.remaining(key, manager, cacheService);
                    case CacheServiceTypeCode.REDIS:
                        if (cacheService instanceof RedisCacheServiceImpl) {
                            return redisExpired.remaining(key, manager, cacheService);
                        }
                    default:
                        throw new UnsupportedOperationException("未知的缓存服务类型");
                }
            }

            @Override
            public void expire(@NotNull String key, @NotNull Duration duration, @NotNull AbstractCacheManager manager,
                @NotNull ICacheService cacheService) {

                expireByType(key, duration, manager, cacheService);

            }

            @Override
            public long remaining(@NotNull String key, @NotNull AbstractCacheManager manager,
                @NotNull ICacheService cacheService) {

                remainByType(key, manager, cacheService);

                return 0;
            }

            @Override
            public void execute() {
                localExpired.execute();
            }
        };
    }

}
