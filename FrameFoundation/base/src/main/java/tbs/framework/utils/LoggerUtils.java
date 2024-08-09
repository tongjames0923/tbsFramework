package tbs.framework.utils;

import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Abstergo
 */
public class LoggerUtils {

    private LogFactory loggerFactory;

    private Map<Class<?>, ILogger> loggerMap = new ConcurrentHashMap<>();

    public LoggerUtils(LogFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    public ILogger getLogger(Class<?> clazz) {
        return loggerMap.computeIfAbsent(clazz, (key) -> loggerFactory.getLogger(clazz.getName()));
    }

    public static final LoggerUtils getInstance() {
        return SingletonHolder.getInstance(LoggerUtils.class);
    }
}
