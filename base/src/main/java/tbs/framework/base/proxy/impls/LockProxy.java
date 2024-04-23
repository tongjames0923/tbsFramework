package tbs.framework.base.proxy.impls;

import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.UuidUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class LockProxy implements IProxy {


    private static ILogger logger;
    private ILock lock;

    private long lockTimeOut;
    private TimeUnit lockTimeUnit;

    public LockProxy(ILock lock, LogUtil util, long lockTimeOut, TimeUnit lockTimeUnit) {
        this.lock = lock;
        if (logger == null) {
            logger = util.getLogger(LockProxy.class.getName());
        }
        this.lockTimeOut = lockTimeOut;
        this.lockTimeUnit = lockTimeUnit;
    }

    @Override
    public <R, P> Optional<R> safeProxy(Function<P, R> function, P param) {
        try {
            return proxy(function, param);
        } catch (Exception e) {
            logger.error(e, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public <R, P> Optional<R> proxy(Function<P, R> function, P param) throws ObtainLockFailException {
        Optional<R> result = Optional.empty();
        String s = UuidUtils.getUuid();
        logger.info(String.format("Locking proxied %s", s));
        try {
            if (lock.tryLock(lockTimeOut, lockTimeUnit)) {
                result = Optional.ofNullable(function.apply(param));
            } else {
                throw new ObtainLockFailException("Failed to obtain lock in time");
            }
        } catch (ObtainLockFailException failException) {
            throw failException;
        } catch (Exception e) {
            logger.error(e, String.format("Lock Error proxied %s: %s", s, e.getMessage()));
        } finally {
            lock.unlock();
        }
        return result;
    }
}
