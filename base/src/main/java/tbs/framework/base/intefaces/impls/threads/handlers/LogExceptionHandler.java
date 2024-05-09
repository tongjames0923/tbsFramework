package tbs.framework.base.intefaces.impls.threads.handlers;

import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

/**
 * @author abstergo
 */
public class LogExceptionHandler implements Thread.UncaughtExceptionHandler {
    private ILogger logger = null;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (logger == null) {
            logger = LogUtil.getInstance().getLogger(LogExceptionHandler.class.getName());
        }
        logger.error(e, "thread:{} msg:{}", t.getName(), e.getMessage());
    }
}
