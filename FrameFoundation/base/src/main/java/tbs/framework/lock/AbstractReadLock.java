package tbs.framework.lock;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Abstergo
 */
public abstract class AbstractReadLock implements ILock {
    private ILock writeLock;
    AtomicLong readCnt = new AtomicLong(0);
    ThreadLocal<Boolean> currentIsRead = ThreadLocal.withInitial(() -> false);

    public AbstractReadLock(ILock writeLock) {
        this.writeLock = writeLock;
    }

    protected boolean isWriting() {
        return writeLock.isLocked();
    }

    @Override
    public boolean tryLock(Duration timeOut) {
        long ed = System.currentTimeMillis() + (timeOut == null ? 1000 * 60 * 30L : timeOut.toMillis());
        while (isWriting()) {
            if (System.currentTimeMillis() < ed) {
                return false;
            }
            Thread.yield();
        }
        readCnt.incrementAndGet();
        currentIsRead.set(true);
        return true;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return readCnt.get() > 0 && currentIsRead.get();
    }

    @Override
    public boolean isLocked() {
        return readCnt.get() > 0;
    }

    @Override
    public void unLock() {
        readCnt.getAndUpdate((x) -> {
            return x <= 0 ? 0 : x - 1;
        });
    }
}
