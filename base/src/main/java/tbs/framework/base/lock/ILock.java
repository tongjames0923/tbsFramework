package tbs.framework.base.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
public interface ILock {
    boolean tryLock(long time, TimeUnit unit, String lockId) throws InterruptedException;

    void lock(String lockId);

    boolean isLocked(String lockId);

    void unlock(String lockId);

}
