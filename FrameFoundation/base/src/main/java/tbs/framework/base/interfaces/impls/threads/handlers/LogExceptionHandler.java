package tbs.framework.base.interfaces.impls.threads.handlers;

import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

/**
 * @author abstergo
 */
public class LogExceptionHandler implements Thread.UncaughtExceptionHandler {

    @AutoLogger
    private ILogger logger = null;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (logger == null) {
            logger = LogFactory.Companion.getInstance().getLogger(LogExceptionHandler.class.getName());
        }
        logger.error(e, "thread:{} msg:{}", t.getName(), e.getMessage());
    }
}
