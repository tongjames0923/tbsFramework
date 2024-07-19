package tbs.framework.lock.impls;

import tbs.framework.lock.ILock;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * @author Abstergo
 */
public class LockAdapter implements ILock {

    Lock lock;

    ThreadLocal<Boolean> flag = new InheritableThreadLocal<Boolean>();
    AtomicBoolean isLock = new AtomicBoolean(false);

    public LockAdapter(Lock lock) {
        this.lock = lock;
    }

    @Override
    public boolean tryLock(Duration timeOut) {
        try {
            boolean f = lock.tryLock(timeOut == null ? 1000 * 60 * 30L : timeOut.toMillis(),
                java.util.concurrent.TimeUnit.MILLISECONDS);
            if (f) {
                flag.set(true);
            }
            isLock.set(f);
            return f;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return flag.get() == null ? false : flag.get();
    }

    @Override
    public boolean isLocked() {
        return isLock.get();
    }

    @Override
    public void unLock() {
        lock.unlock();
        flag.set(false);
        isLock.set(false);
    }
}
