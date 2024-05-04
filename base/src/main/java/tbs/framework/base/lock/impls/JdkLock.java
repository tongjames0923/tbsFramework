package tbs.framework.base.lock.impls;

import tbs.framework.base.lock.ILock;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * @author abstergo
 */
public class JdkLock implements ILock {

    private final ILogger logger;
    Function<String, Lock> lockProvider;
    private ConcurrentHashMap<String, Boolean> lockMap = new ConcurrentHashMap<>();

    public JdkLock(final Function<String, Lock> lock, final LogUtil util) {
        if (lock == null) {
            throw new NullPointerException("lock provider is null");
        }
        this.lockProvider = lock;
        this.logger = util.getLogger(JdkLock.class.getName());
    }

    private Lock getLock(String l) {
        return this.lockProvider.apply(l);
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit, String lockId) throws InterruptedException {
        this.logger.trace(String.format("Trying to acquire lock %s %s ,lockId:%s", time, unit.toString(), lockId));
        final boolean set = getLock(lockId).tryLock(time, unit);
        this.lockMap.put(lockId, set);
        return set;
    }

    @Override
    public boolean isLocked(String lockId) {
        return lockMap.getOrDefault(lockId, false);
    }

    @Override
    public void lock(String lockId) {
        this.getLock(lockId).lock();
        lockMap.put(lockId, true);
        this.logger.trace("Lock acquired");
    }

    @Override
    public void unlock(String lockId) {
        if (isLocked(lockId)) {
            this.getLock(lockId).unlock();
            this.logger.trace("Unlocked " + lockId);
        } else {
            this.logger.warn("locker already unlocked " + lockId);
        }
    }

}
