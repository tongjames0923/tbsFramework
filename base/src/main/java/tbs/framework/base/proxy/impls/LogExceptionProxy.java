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
    public LogExceptionProxy(final LogUtil util) {
        this.logger = util.getLogger(LogExceptionProxy.class.getName());
    }

    @Override
    public <R, P> Optional<R> proxy(final FunctionWithThrows<P, R, Throwable> function, final P param) throws Throwable {
        final String uuid = UuidUtils.getUuid();
        this.logger.trace("Proxying [" + uuid + "]");
        Optional<R> result = Optional.empty();
        try {
            result = Optional.ofNullable(function.apply(param));
            this.logger.trace(String.format("Proxying [%s] result returned", uuid));
        } catch (final Exception ex) {
            this.logger.error(ex, String.format("Proxying [%s] failed.message:[%s]", uuid, ex.getMessage()));
        }
        return result;
    }

    @Override
    public <R, P> Optional<R> safeProxy(final FunctionWithThrows<P, R, Throwable> function, final P param) {
        try {
            return this.proxy(function, param);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
