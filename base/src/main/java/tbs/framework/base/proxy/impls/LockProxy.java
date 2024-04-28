package tbs.framework.base.proxy.impls;

import tbs.framework.base.intefaces.FunctionWithThrows;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.UuidUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>LockProxy class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class LockProxy implements IProxy {

    private static ILogger logger;
    private final ILock lock;

    private final long lockTimeOut;
    private final TimeUnit lockTimeUnit;

    /**
     * <p>Constructor for LockProxy.</p>
     *
     * @param lock a {@link tbs.framework.base.lock.ILock} object
     * @param util a {@link tbs.framework.base.utils.LogUtil} object
     * @param lockTimeOut a long
     * @param lockTimeUnit a {@link java.util.concurrent.TimeUnit} object
     */
    public LockProxy(ILock lock, LogUtil util, long lockTimeOut, TimeUnit lockTimeUnit) {
        this.lock = lock;
        if (null == LockProxy.logger) {
            logger = util.getLogger(LockProxy.class.getName());
        }
        this.lockTimeOut = lockTimeOut;
        this.lockTimeUnit = lockTimeUnit;
    }

    /** {@inheritDoc} */
    @Override
    public <R, P> Optional<R> safeProxy(FunctionWithThrows<P, R, Throwable> function, P param) {
        try {
            return proxy(function, param);
        } catch (Throwable e) {
            logger.error(e, e.getMessage());
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public <R, P> Optional<R> proxy(FunctionWithThrows<P, R, Throwable> function, P param) throws Throwable {
        Optional<R> result = Optional.empty();
        String s = UuidUtils.getUuid();
        logger.trace(String.format("Locking proxied %s", s));
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(lockTimeOut, lockTimeUnit);
            if (isLocked) {
                result = Optional.ofNullable(function.apply(param));
            } else {
                throw new ObtainLockFailException("Failed to obtain lock in time");
            }
        } finally {
            lock.unlock();
        }
        return result;
    }
}
