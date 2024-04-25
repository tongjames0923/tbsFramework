package tbs.framework.base.log.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tbs.framework.base.log.ILogger;

public class Slf4jLogger implements ILogger {

    private final Logger log;
    private final String name;

    public Slf4jLogger(String name) {
        this.name = name;
        log = LoggerFactory.getLogger(this.name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void trace(String message) {
        log.trace(message);
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void error(Throwable ex, String message) {
        log.error(message, ex);
    }
}
