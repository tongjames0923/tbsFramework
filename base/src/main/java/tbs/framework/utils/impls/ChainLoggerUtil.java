package tbs.framework.utils.impls;

import tbs.framework.log.AbstractLogChainProvider;
import tbs.framework.log.ILogger;
import tbs.framework.log.impls.ChainLogger;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class ChainLoggerUtil extends AbstractBeanLogUtil {

    public static final String LOGGER_CHAIN = "LOGGER_CHAIN";

    @Resource(name = LOGGER_CHAIN)
    private AbstractLogChainProvider logChainProvider;

    @Override
    protected ILogger newLogger(String name) {
        return new ChainLogger(logChainProvider, name);
    }

}
