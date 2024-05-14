package tbs.framework.auth.interfaces.impls;

import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.log.ILogger;
import tbs.framework.utils.LogUtil;

/**
 * 基础异常处理
 *
 * @author abstergo
 */
public class SimpleLogErrorHandler implements IErrorHandler {

    private final ILogger logger;

    public SimpleLogErrorHandler(final LogUtil logUtil) {
        this.logger = logUtil.getLogger(SimpleLogErrorHandler.class.getName());
    }

    @Override
    public Object handleError(final Throwable ex) {
        this.logger.error(ex, ex.getMessage());
        return null;
    }
}
