package tbs.framework.base.lock;

import java.util.concurrent.TimeUnit;

public interface ILock {
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    void lock();

    void unlock();

}
