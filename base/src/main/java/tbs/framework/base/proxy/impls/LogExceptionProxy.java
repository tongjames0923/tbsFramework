package tbs.framework.base.proxy.impls;

import tbs.framework.base.intefaces.FunctionWithThrows;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.UuidUtils;

import java.util.Optional;

/**
 * <p>LogExceptionProxy class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class LogExceptionProxy implements IProxy {

    private final ILogger logger;

    /**
     * <p>Constructor for LogExceptionProxy.</p>
     *
     * @param util a {@link tbs.framework.base.utils.LogUtil} object
     */
    public LogExceptionProxy(LogUtil util) {
        logger = util.getLogger(LogExceptionProxy.class.getName());
    }

    /** {@inheritDoc} */
    @Override
    public <R, P> Optional<R> proxy(FunctionWithThrows<P, R, Throwable> function, P param) throws Throwable {
        String uuid = UuidUtils.getUuid();
        logger.trace("Proxying [" + uuid + "]");
        Optional<R> result = Optional.empty();
        try {
            result = Optional.ofNullable(function.apply(param));
            logger.trace(String.format("Proxying [%s] result returned", uuid));
        } catch (Exception ex) {
            logger.error(ex, String.format("Proxying [%s] failed.message:[%s]", uuid, ex.getMessage()));
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public <R, P> Optional<R> safeProxy(FunctionWithThrows<P, R, Throwable> function, P param) {
        try {
            return proxy(function, param);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
