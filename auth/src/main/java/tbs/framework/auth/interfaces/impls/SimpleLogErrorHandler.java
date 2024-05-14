package tbs.framework.auth.interfaces.impls;

import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

/**
 * 基础异常处理
 *
 * @author abstergo
 */
public class SimpleLogErrorHandler implements IErrorHandler {

    @AutoLogger
    private ILogger logger;

    public SimpleLogErrorHandler() {

    }

    @Override
    public Object handleError(final Throwable ex) {
        this.logger.error(ex, ex.getMessage());
        return null;
    }
}
