package tbs.framework.base.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public interface ILock {
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    void lock();

    boolean isLocked();

    void unlock();

}
