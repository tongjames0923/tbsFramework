package tbs.framework.redis.impls;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tbs.framework.cache.ICacheService;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class RedisCacheService implements ICacheService {

    @Resource
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(String key, Object value, boolean override) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        if (override) {
            vo.set(keyGeneration(key), value);
        } else {
            vo.setIfAbsent(keyGeneration(key), value);
        }
    }

    @Override
    public Optional get(String key, boolean isRemove, long delay) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        if (isRemove || delay <= 0) {
            return Optional.ofNullable(vo.getAndExpire(keyGeneration(key), Duration.ofSeconds(delay)));
        }
        return Optional.ofNullable(vo.get(keyGeneration(key)));
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(keyGeneration(key));
    }

    @Override
    public void clear() {
        redisTemplate.delete(keyGeneration("*"));
    }

    @Override
    public void expire(String key, long seconds) {
        redisTemplate.expire(keyGeneration(key), seconds, TimeUnit.SECONDS);
    }

    @Override
    public long remain(String key) {
        Long re = redisTemplate.getExpire(keyGeneration(key), TimeUnit.SECONDS);
        return re == null ? 0 : re;
    }
}
