package tbs.framework.redis.impls;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.IkeyMixer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class RedisCacheService implements ICacheService, IkeyMixer {
    @Override
    public String mixKey(String key) {
        return "Cache-" + key;
    }

    @Resource
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void put(String key, Object value, boolean override) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        if (override) {
            vo.set(mixKey(key), value);
        } else {
            vo.setIfAbsent(mixKey(key), value);
        }
    }

    @Override
    public Optional get(String key, boolean isRemove, long delay) {
        ValueOperations<String, Object> vo = redisTemplate.opsForValue();
        if (isRemove && delay > 0) {
            return Optional.ofNullable(vo.getAndExpire(mixKey(key), Duration.ofSeconds(delay)));
        }
        return Optional.ofNullable(vo.get(mixKey(key)));
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(mixKey(key));
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(mixKey(key));
    }

    @Override
    public void clear() {
        redisTemplate.delete(mixKey("*"));
    }

    @Override
    public void expire(String key, long seconds) {
        redisTemplate.expire(mixKey(key), seconds, TimeUnit.SECONDS);
    }

    @Override
    public long remain(String key) {
        Long re = redisTemplate.getExpire(mixKey(key), TimeUnit.SECONDS);
        return re == null ? 0 : re;
    }
}
