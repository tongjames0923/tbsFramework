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
    public boolean tryLock(Duration timeOut) {
        try {
            return lock.tryLock(timeOut.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        if (isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
