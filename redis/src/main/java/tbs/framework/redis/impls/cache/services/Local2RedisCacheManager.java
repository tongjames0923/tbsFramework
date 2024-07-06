package tbs.framework.redis.impls.cache.services;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.cache.impls.hooks.LocalTimeoutEliminateHook;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;
import tbs.framework.cache.managers.AbstractTimebaseHybridCacheManager;
import tbs.framework.redis.impls.cache.hooks.SimpleRedisHook;

/**
 * TODO 待完成复合缓存功能测试
 * @author abstergo
 */
public class Local2RedisCacheManager extends AbstractTimebaseHybridCacheManager {

    @Override
    public void afterPropertiesSet() throws Exception {
        this.addService(SpringUtil.getBean(ConcurrentMapCacheServiceImpl.class));
        this.addService(SpringUtil.getBean(RedisCacheServiceImpl.class));
        this.addHook(new LocalTimeoutEliminateHook());
        this.addHook(new SimpleRedisHook());
        setService(0);
    }

    @Override
    public void collaborativeWriting(String key, Object value, boolean overwrite, int flag) {
        switch (flag) {
            case WRITE_PUT:
                if (getCacheService().cacheSize() > 5) {
                    setService((serviceIndex() + 1) % serviceCount());
                }
                break;
            case WRITE_REMOVE:
                int idx = selectService((s, i) -> {
                    return s.exists(key);
                }) % serviceCount();
                setService(idx);
                break;
            default:
                break;
        }
    }

    @Override
    public void collaborativeReading(String key, int flag) {
        switch (flag) {

        }
    }
}
