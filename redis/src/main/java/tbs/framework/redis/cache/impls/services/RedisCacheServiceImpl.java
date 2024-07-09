package tbs.framework.redis.cache.impls.services;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IkeyMixer;
import tbs.framework.redis.IRedisTemplateSupport;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class RedisCacheServiceImpl implements ICacheService, IRedisTemplateSupport, IkeyMixer {
    @Override
    public String mixKey(String key) {
        return property.getCacheKeyPrefix() + key;
    }

    @Resource
    RedisProperty property;

    private RedisTemplate<String, Object> redisTemplate = null;

    @Override
    public RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = SpringUtil.getBean(property.getCacheSource());
        }
        return redisTemplate;
    }

    @Override
    public void put(String key, Object value, boolean override) {
        ValueOperations<String, Object> vo = getRedisTemplate().opsForValue();
        if (override) {
            vo.set(mixKey(key), value);
        } else {
            vo.setIfAbsent(mixKey(key), value);
        }
    }

    @Override
    public Object get(String key) {
        ValueOperations<String, Object> vo = getRedisTemplate().opsForValue();
        return vo.get(mixKey(key));
    }

    @Override
    public boolean exists(String key) {
        return getRedisTemplate().hasKey(mixKey(key));
    }

    @Override
    public void remove(String key) {
        getRedisTemplate().delete(mixKey(key));
    }

    @Override
    public void clear() {
        getRedisTemplate().delete(mixKey("*"));
    }

    @Override
    public long cacheSize() {
        return getRedisTemplate().opsForValue().size(mixKey("*"));
    }
}
