package tbs.framework.proxy.impls;

import tbs.framework.base.intefaces.FunctionWithThrows;
import tbs.framework.log.ILogger;
import tbs.framework.proxy.IProxy;
import tbs.framework.utils.LogUtil;

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
     * @param util a {@link LogUtil} object
     */
    public LogExceptionProxy(final LogUtil util) {
        this.logger = util.getLogger(LogExceptionProxy.class.getName());
    }

    @Override
    public <R, P> Optional<R> proxy(final FunctionWithThrows<P, R, Throwable> function, final P param,
        IProxyAdditionalInfo additional) throws Throwable {
        Optional<R> result = Optional.empty();
        try {
            result = Optional.ofNullable(function.apply(param));
        } catch (final Exception ex) {
            this.logger.error(ex, String.format("Proxying failed.message:[%s]", ex.getMessage()));
        }
        return result;
    }

    @Override
    public <R, P> Optional<R> safeProxy(final FunctionWithThrows<P, R, Throwable> function, final P param,
        IProxyAdditionalInfo addtional) {
        try {
            return this.proxy(function, param, addtional);
        } catch (final Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
