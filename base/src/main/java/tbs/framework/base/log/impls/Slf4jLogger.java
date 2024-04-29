package tbs.framework.base.log.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tbs.framework.base.log.ILogger;

public class Slf4jLogger implements ILogger {

    private final Logger log;
    private final String name;

    public Slf4jLogger(final String name) {
        this.name = name;
        this.log = LoggerFactory.getLogger(this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void trace(final String message) {
        this.log.trace(message);
    }

    @Override
    public void debug(final String message) {
        this.log.debug(message);
    }

    @Override
    public void info(final String message) {
        this.log.info(message);
    }

    @Override
    public void warn(final String message) {
        this.log.warn(message);
    }

    @Override
    public void error(final Throwable ex, final String message) {
        this.log.error(message, ex);
    }
}
