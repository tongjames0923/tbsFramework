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

    private final AtomicBoolean isLocked = new AtomicBoolean(false);

    public JdkLock(final Lock lock, final LogUtil util) {
        this.l = lock;
        this.logger = util.getLogger(JdkLock.class.getName());
    }


    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        this.logger.trace(String.format("Trying to acquire lock %s %s", time, unit.toString()));
        final boolean set = this.l.tryLock(time, unit);
        this.isLocked.set(set);
        return set;
    }

    @Override
    public boolean isLocked() {
        return this.isLocked.get();
    }

    @Override
    public void lock() {
        this.l.lock();
        this.isLocked.set(true);
        this.logger.trace("Lock acquired");
    }

    @Override
    public void unlock() {
        if (this.isLocked.get()) {
            this.l.unlock();
            this.logger.trace("Unlocked");
        } else {
            this.logger.warn("locker already unlocked");
        }
    }

}
