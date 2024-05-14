package tbs.framework.proxy.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.base.intefaces.FunctionWithThrows;
import tbs.framework.lock.ILock;
import tbs.framework.lock.expections.ObtainLockFailException;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.proxy.IProxy;
import tbs.framework.utils.LogUtil;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>LockProxy class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class LockProxy implements IProxy {

    private static LockProxy lockProxy;

    public static final LockProxy getInstance() {
        if (lockProxy == null) {
            lockProxy = SpringUtil.getBean(LockProxy.class);
        }
        return lockProxy;
    }

    @AutoLogger
    private static ILogger logger;

    private ILock lock;
    private Class<? extends ILock> lockClass;

    private BigDecimal lockId = new BigDecimal(0);

    ILock getLock() {
        if (null == lock) {
            lock = SpringUtil.getBean(lockClass);
        }
        return lock;
    }

    private final long lockTimeOut;
    private final TimeUnit lockTimeUnit;

    private static final String addtionalLockIdKey = "LOCK_ID";

    /**
     * <p>Constructor for LockProxy.</p>
     *
     * @param util         a {@link LogUtil} object
     * @param lockTimeOut  a long
     * @param lockTimeUnit a {@link java.util.concurrent.TimeUnit} object
     */
    public LockProxy(Class<? extends ILock> lock, final LogUtil util, final long lockTimeOut,
        final TimeUnit lockTimeUnit) {
        this.lockClass = lock;
        this.lockTimeOut = lockTimeOut;
        this.lockTimeUnit = lockTimeUnit;
    }

    @Override
    public <R, P> Optional<R> safeProxy(final FunctionWithThrows<P, R, Throwable> function, final P param,
        IProxyAdditionalInfo addtional) {
        try {
            return this.proxy(function, param, addtional);
        } catch (final Throwable e) {
            LockProxy.logger.error(e, e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * 代理锁
     *
     * @param <R>
     * @param <P>
     * @param function   代理方法
     * @param param      参数 代理方法参数
     * @param additional 锁名称参数 若为空则使用默认锁名""
     * @return
     * @throws Throwable
     */
    @Override
    public <R, P> Optional<R> proxy(final FunctionWithThrows<P, R, Throwable> function, final P param,
        IProxyAdditionalInfo additional) throws Throwable {
        Optional<R> result = Optional.empty();
        String lockId = "";
        if (null != additional) {
            lockId = additional.getInfoAs(String.class, addtionalLockIdKey);
        }
        logger.trace("{} begin to lock", lockId);
        boolean isLocked = false;
        try {
            isLocked = this.getLock().tryLock(this.lockTimeOut, this.lockTimeUnit, lockId);
            if (isLocked) {
                logger.trace("{} locked", lockId);
                result = Optional.ofNullable(function.apply(param));
            } else {
                throw new ObtainLockFailException("Failed to obtain lock in time");
            }
        } finally {
            this.getLock().unlock(lockId);
            logger.trace("{}  unlocked", lockId);
        }
        return result;
    }

    public void quickLock(Runnable task, String lockId) {
        safeProxy((p) -> {
            task.run();
            return null;
        }, null, new SimpleLockAddtionalInfo(lockId));
    }
}
