package tbs.framework.redis.impls.lock.providers;

import org.redisson.api.RedissonClient;
import tbs.framework.lock.ILock;
import tbs.framework.lock.ILockProvider;
import tbs.framework.redis.impls.lock.RedissonLockImpl;

import javax.annotation.Resource;

/**
 * 使用Redisson客户端提供锁的实现。
 *
 * <p>本类实现了ILockProvider接口，用于提供Redisson锁的实例。通过注入RedissonClient，可以方便地创建和配置锁。
 *
 * @author Abstergo
 * @since 1.0
 */
public class RedissonLockProvider implements ILockProvider {

    /**
     * Redisson客户端，用于创建和操作锁。
     */
    @Resource
    private RedissonClient redissonClient;

    /**
     * 获取锁实例。
     *
     * <p>通过传入的目标对象，创建一个对应的Redisson锁实例。
     *
     * @param target 目标对象
     * @return 锁实例
     */
    @Override
    public ILock getLocker(Object target) {
        return new RedissonLockImpl(redissonClient, target);
    }
}
