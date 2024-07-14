package tbs.framework.redis.impls.lock;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tbs.framework.mq.IMessageHandleBlocker;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 使用redis实现的分布式任务阻拦锁，一个锁id在规定时限a内有效或解锁后在规定时间b内持续有效
 *
 * @author abstergo
 */
public class RedisTaskBlockLock extends IMessageHandleBlocker {

    public RedisTaskBlockLock() {
    }

    @Resource
    @Lazy
    RedisTemplate<String, Object> redisTemplate;

    private ValueOperations<String, Object> valueOperations;

    private ValueOperations<String, Object> getValueOperations() {
        if (valueOperations == null) {
            valueOperations = redisTemplate.opsForValue();
        }
        return valueOperations;
    }

    private String key(String l) {
        return "BLOCK_KEY_REDIS_LOCK:" + l;
    }

    @Override
    protected boolean lock(String id, Duration alive) {
        return redisTemplate.opsForValue().setIfAbsent(key(id), true, alive);
    }

    @Override
    protected void unlock(String id, Duration delay) {
        redisTemplate.expire(key(id), delay);
    }
}
