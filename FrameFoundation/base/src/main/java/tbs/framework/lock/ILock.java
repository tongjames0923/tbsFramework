package tbs.framework.lock;

import java.time.Duration;

/**
 * 锁
 *
 * @author abstergo
 */
public interface ILock {

    boolean tryLock(Duration timeOut) throws InterruptedException;

    boolean isHeldByCurrentThread();

    boolean isLocked();

    void unLock();

}
