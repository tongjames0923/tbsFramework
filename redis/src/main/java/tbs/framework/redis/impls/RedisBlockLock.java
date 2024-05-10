package tbs.framework.redis.impls;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.expections.ObtainLockFailException;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class RedisBlockLock implements ILock {

    @Resource
    @Lazy
    RedisTemplate<String, Object> redisTemplate;

    private String key(String l) {
        return "BLOCK_KEY_REDIS_LOCK:" + l;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit, String lockId) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void lock(String lockId) {
        boolean locked = redisTemplate.opsForValue().setIfAbsent(key(lockId), true, 5, TimeUnit.MINUTES);
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
        redisTemplate.expire(key(lockId), 50, TimeUnit.MILLISECONDS);
    }
}
