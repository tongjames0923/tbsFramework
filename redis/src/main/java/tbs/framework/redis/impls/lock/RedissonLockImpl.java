package tbs.framework.redis.impls.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tbs.framework.base.lock.ILock;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class RedissonLockImpl implements ILock {
    @Resource
    RedissonClient redissonClient;

    @Override
    public boolean tryLock(long time, TimeUnit unit, String lockId) throws InterruptedException {
        return redissonClient.getLock(lockId).tryLock(time, unit);
    }

    @Override
    public void lock(String lockId) {
        redissonClient.getLock(lockId).lock();
    }

    @Override
    public boolean isLocked(String lockId) {
        return redissonClient.getLock(lockId).isLocked();
    }

    @Override
    public void unlock(String lockId) {
        RLock lock = redissonClient.getLock(lockId);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
