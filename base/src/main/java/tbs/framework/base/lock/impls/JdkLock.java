package tbs.framework.base.lock.impls;

import tbs.framework.base.lock.ILock;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * @author abstergo
 */
public class JdkLock implements ILock {

    private final ILogger logger;
    Lock l;

    private AtomicBoolean isLocked = new AtomicBoolean(false);

    public JdkLock(Lock lock, LogUtil util) {
        l = lock;
        logger = util.getLogger(JdkLock.class.getName());
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        logger.trace(String.format("Trying to acquire lock %s %s", time, unit.toString()));
        boolean set = l.tryLock(time, unit);
        isLocked.set(set);
        return set;
    }

    @Override
    public boolean isLocked() {
        return isLocked.get();
    }

    @Override
    public void lock() {
        l.lock();
        isLocked.set(true);
        logger.trace("Lock acquired");
    }

    @Override
    public void unlock() {
        if (isLocked.get()) {
            l.unlock();
            logger.trace("Unlocked");
        } else {
            logger.warn("locker already unlocked");
        }
    }

}
