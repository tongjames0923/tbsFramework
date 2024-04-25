package tbs.framework.auth.interfaces.impls;

import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

public class SimpleLogErrorHandler implements IErrorHandler {

    private ILogger logger;

    public SimpleLogErrorHandler(LogUtil logUtil) {
        logger = logUtil.getLogger(SimpleLogErrorHandler.class.getName());
    }

    @Override
    public Object handleError(Throwable ex) {
        logger.error(ex, ex.getMessage());
        return null;
    }
}
