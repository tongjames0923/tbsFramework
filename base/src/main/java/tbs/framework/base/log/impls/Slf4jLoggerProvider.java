package tbs.framework.base.log.impls;

import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.log.ILogger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Slf4jLoggerProvider implements ILogProvider {
    private static final ConcurrentMap<String, ILogger> loggers = new ConcurrentHashMap<>(10);


    @Override
    public ILogger getLogger(String name) {
        if (loggers.containsKey(name)) {
            return loggers.get(name);
        } else {
            ILogger logger = new Slf4jLogger(name);
            loggers.put(name, logger);
            return logger;
        }
    }
}
