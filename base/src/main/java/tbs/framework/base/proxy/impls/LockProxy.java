package tbs.framework.base.proxy.impls;

import tbs.framework.base.lock.ILock;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.UuidUtils;

import java.util.Optional;
import java.util.function.Function;

public class LockProxy implements IProxy {


    private static ILogger logger;
    private ILock lock;

    public LockProxy(ILock lock, LogUtil util) {
        this.lock = lock;
        if (logger == null) {
            logger = util.getLogger(LockProxy.class.getName());
        }
    }

    @Override
    public <R, P> Optional<R> proxy(Function<P, R> function, P param) {
        Optional<R> result = Optional.empty();
        String s = UuidUtils.getUuid();
        logger.info(String.format("Locking proxied %s", s));
        lock.lock();
        try {
            result = Optional.ofNullable(function.apply(param));
        } catch (Exception e) {
            logger.error(e, String.format("Lock Error proxied %s: %s", s, e.getMessage()));
        }
        lock.unlock();
        return result;
    }
}
