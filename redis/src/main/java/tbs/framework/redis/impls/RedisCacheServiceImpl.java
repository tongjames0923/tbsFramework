package tbs.framework.redis.impls;

import cn.hutool.extra.spring.SpringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.ITimeBaseSupportedHook;
import tbs.framework.cache.IkeyMixer;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class RedisCacheServiceImpl implements ICacheService, IkeyMixer, ITimeBaseSupportedHook {
    @Override
    public String mixKey(String key) {
        return property.getCacheKeyPrefix() + key;
    }

    @Resource
    RedisProperty property;

    private RedisTemplate<String, Object> redisTemplate = null;

    private RedisTemplate<String, Object> getRedisTemplate() {
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

    @Override
    public void onSetDelay(String key, Duration delay, ICacheService service) {
        getRedisTemplate().expire(mixKey(key), delay);
    }

    @Override
    public void onTimeout(String key, ICacheService service) {

    }

    @Override
    public long remainingTime(String key, ICacheService service) {
        return getRedisTemplate().getExpire(mixKey(key), TimeUnit.SECONDS);
    }

    @Override
    public void onSetCache(@NotNull String key, Object value, boolean override, ICacheService cacheService) {

    }

    @Override
    public Object onGetCache(String key, ICacheService cacheService, Object value) {
        return value;
    }

    @Override
    public void onRemoveCache(String key, ICacheService cacheService) {

    }

    @Override
    public void onClearCache(ICacheService cacheService) {

    }

    @Override
    public void onTestCache(String key, ICacheService cacheService) {

    }
}
