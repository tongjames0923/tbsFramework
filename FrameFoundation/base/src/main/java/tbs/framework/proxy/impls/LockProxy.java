package tbs.framework.proxy.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.base.interfaces.FunctionWithThrows;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.lock.ILock;
import tbs.framework.lock.expections.ObtainLockFailException;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.proxy.IProxy;

import java.lang.ref.WeakReference;
import java.time.Duration;
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

    private final long lockTimeOut;
    private final TimeUnit lockTimeUnit;

    /**
     * <p>Constructor for LockProxy.</p>
     *
     * @param util         a {@link LogFactory} object
     * @param lockTimeOut  a long
     * @param lockTimeUnit a {@link java.util.concurrent.TimeUnit} object
     */
    public LockProxy(final LogFactory util, final long lockTimeOut, final TimeUnit lockTimeUnit) {
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
        ILock lk = null;
        if (null != additional) {
            lk = additional.getInfoAs(ILock.class, null);
        }
        WeakReference<IProxyAdditionalInfo> additionalInfoWeakReference = new WeakReference<>(additional);
        if (lk == null) {
            throw new ObtainLockFailException("no lock get from additional info");
        }

        try {
            logger.debug("try to lock for {}", lk.toString());
            boolean isLocked = lk.tryLock(Duration.ofMillis(TimeUnit.MILLISECONDS.convert(lockTimeOut, lockTimeUnit)));
            if (isLocked) {
                logger.debug("locked for {}", lk.toString());
                R r = function.apply(param);
                result = Optional.ofNullable(r);
            } else {
                throw new ObtainLockFailException("Failed to obtain lock in time");
            }
        } finally {
            logger.debug("unlock for {}", lk.toString());
            lk.unLock();
        }
        lk = null;
        additional = null;
        return result;
    }

    public void quickLock(Runnable task, ILock lock) {
        safeProxy((p) -> {
            task.run();
            return null;
        }, null, new SimpleLockAddtionalInfo(lock));
    }
}
