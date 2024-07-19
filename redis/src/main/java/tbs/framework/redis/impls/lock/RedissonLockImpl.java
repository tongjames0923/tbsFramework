package tbs.framework.redis.impls.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import tbs.framework.lock.ILock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public class RedissonLockImpl implements ILock {

    private RedissonClient redissonClient;

    private Object target;

    public RedissonLockImpl(RedissonClient redissonClient, Object target) {
        this.redissonClient = redissonClient;
        this.target = target;
    }

    @Override
    public boolean tryLock(Duration timeOut) {
        try {
            return getLock().tryLock(timeOut.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isHeldByCurrentThread() {
        return getLock().isHeldByCurrentThread();
    }

    @Override
    public boolean isLocked() {
        return getLock().isLocked();
    }

    private RLock getLock() {
        return redissonClient.getLock(target.toString());
    }

    @Override
    public void unLock() {
        if (isHeldByCurrentThread()) {
            getLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "RedissonLockImpl{" + "redissonClient=" + redissonClient + ", target=" + target + '}';
    }
}
