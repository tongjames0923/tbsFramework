package tbs.framework.base.utils.impls;

import tbs.framework.base.log.ILogger;
import tbs.framework.base.log.impls.Slf4jLogger;
import tbs.framework.base.utils.LogUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Abstergo
 */
public class Slf4jLoggerUtil extends LogUtil {

    private static final ConcurrentMap<String, ILogger> loggers = new ConcurrentHashMap<>(10);

    @Override
    public ILogger getLogger(final String name) {
        if (Slf4jLoggerUtil.loggers.containsKey(name)) {
            return Slf4jLoggerUtil.loggers.get(name);
        } else {
            final ILogger logger = new Slf4jLogger(name);
            Slf4jLoggerUtil.loggers.put(name, logger);
            return logger;
        }
    }
}
