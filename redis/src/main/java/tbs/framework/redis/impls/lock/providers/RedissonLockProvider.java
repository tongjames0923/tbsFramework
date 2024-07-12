package tbs.framework.redis.impls.lock.providers;

import org.redisson.api.RedissonClient;
import tbs.framework.lock.ILock;
import tbs.framework.lock.ILockProvider;
import tbs.framework.redis.impls.lock.RedissonLockImpl;

import javax.annotation.Resource;

public class RedissonLockProvider implements ILockProvider {

    @Resource
    RedissonClient redissonClient;

    @Override
    public ILock getLocker(Object target) {
        return new RedissonLockImpl(redissonClient, target);
    }
}
