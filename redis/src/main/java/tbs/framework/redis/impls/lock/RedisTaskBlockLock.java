package tbs.framework.redis.impls.lock;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import tbs.framework.lock.ILock;
import tbs.framework.lock.expections.ObtainLockFailException;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis实现的分布式任务阻拦锁，一个锁id在规定时限a内有效或解锁后在规定时间b内持续有效
 * @author abstergo
 */
public class RedisTaskBlockLock implements ILock {

    private Duration maxLockAliveTime = Duration.ofMinutes(5);

    private Duration unLockDelayTime = Duration.ofSeconds(30);

    public RedisTaskBlockLock(Duration maxLockAliveTime, Duration unLockDelayTime) {
        this.maxLockAliveTime = maxLockAliveTime;
        this.unLockDelayTime = unLockDelayTime;
    }

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
    public boolean tryLock(long time, TimeUnit unit, String lockId) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lock(String lockId) {
        boolean locked = redisTemplate.opsForValue().setIfAbsent(key(lockId), true, maxLockAliveTime);
        if (!locked) {
            throw new ObtainLockFailException("lock has been obtained.");
        }
    }

    @Override
    public boolean isLocked(String lockId) {
        return redisTemplate.hasKey(key(lockId));
    }

    @Override
    public void unlock(String lockId) {
        redisTemplate.expire(key(lockId), unLockDelayTime);
    }
}
