package tbs.framework.utils;

import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Abstergo
 */
public class LoggerUtils {

    @Resource
    private LogFactory loggerFactory;

    private Map<Class<?>, ILogger> loggerMap = new ConcurrentHashMap<>();

    public ILogger getLogger(Class<?> clazz) {
        ILogger logger = null;
        synchronized (clazz) {
            logger = loggerMap.get(clazz);
            if (logger == null) {
                logger = loggerFactory.getLogger(clazz.getPackageName() + clazz.getName());
                loggerMap.put(clazz, logger);
            }
        }
        return logger;
    }

    private static LoggerUtils INSTANCE = null;

    public static final LoggerUtils getInstance() {
        synchronized (LoggerUtils.class) {
            if (INSTANCE == null) {
                INSTANCE = new LoggerUtils();
                BeanUtil.registerBean(INSTANCE, "loggerUtils");
            }
        }
        return INSTANCE;
    }
}
