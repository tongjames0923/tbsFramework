package tbs.framework.lock.impls;

import tbs.framework.lock.ILock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author abstergo
 */
public class ReentrantLockImpl implements ILock {
    ReentrantLock lock = new ReentrantLock();

    @Override
    public boolean tryLock(Duration timeOut) throws InterruptedException {
        return lock.tryLock(timeOut.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }

    @Override
    public boolean isLocked() {
        return lock.isLocked();
    }

    @Override
    public void unLock() {
        if (isLocked() && isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
