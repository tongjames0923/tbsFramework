package tbs.framework.auth.interfaces.impls;

import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

/**
 * 基础异常处理
 *
 * @author abstergo
 */
public class SimpleLogErrorHandler implements IErrorHandler {

    private final ILogger logger;

    public SimpleLogErrorHandler(LogUtil logUtil) {
        logger = logUtil.getLogger(SimpleLogErrorHandler.class.getName());
    }

    @Override
    public Object handleError(Throwable ex) {
        logger.error(ex, ex.getMessage());
        return null;
    }
}
