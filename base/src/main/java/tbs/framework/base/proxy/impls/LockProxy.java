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
    public LockProxy(final ILock lock, final LogUtil util, final long lockTimeOut, final TimeUnit lockTimeUnit) {
        this.lock = lock;
        if (null == logger) {
            LockProxy.logger = util.getLogger(LockProxy.class.getName());
        }
        this.lockTimeOut = lockTimeOut;
        this.lockTimeUnit = lockTimeUnit;
    }

    @Override
    public <R, P> Optional<R> safeProxy(final FunctionWithThrows<P, R, Throwable> function, final P param) {
        try {
            return this.proxy(function, param);
        } catch (final Throwable e) {
            LockProxy.logger.error(e, e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public <R, P> Optional<R> proxy(final FunctionWithThrows<P, R, Throwable> function, final P param) throws Throwable {
        Optional<R> result = Optional.empty();
        final String s = UuidUtils.getUuid();
        LockProxy.logger.trace(String.format("Locking proxied %s", s));
        boolean isLocked = false;
        try {
            isLocked = this.lock.tryLock(this.lockTimeOut, this.lockTimeUnit);
            if (isLocked) {
                result = Optional.ofNullable(function.apply(param));
            } else {
                throw new ObtainLockFailException("Failed to obtain lock in time");
            }
        } finally {
            this.lock.unlock();
        }
        return result;
    }
}
