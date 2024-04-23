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
    public Object handleError(Throwable ex, Class returnType, Object result) {
        logger.error(ex, ex.getMessage());
        try {
            result = returnType.getConstructor().newInstance();
        } catch (Exception e) {
            logger.error(e, e.getMessage());
        }
        return result;
    }
}
