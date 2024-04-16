package tbs.framework.base.lock.impls;

import tbs.framework.base.lock.ILock;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class JDKLock implements ILock {

    private final ILogger logger;
    Lock l;

    public JDKLock(Lock lock, LogUtil util) {
        l = lock;
        logger = util.getLogger(JDKLock.class.getName());
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        logger.info(String.format("Trying to acquire lock %s", time));
        return l.tryLock(time, unit);
    }

    @Override
    public void lock() {
        logger.info(String.format("Lock acquired"));
        l.lock();
    }

    @Override
    public void unlock() {
        logger.info(String.format("Unlock acquired"));
        l.unlock();
    }

}
