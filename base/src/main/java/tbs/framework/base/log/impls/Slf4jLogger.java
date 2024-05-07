package tbs.framework.base.log.impls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tbs.framework.base.log.ILogger;

/**
 * 默认slf4j日志
 * @author Abstergo
 */
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
    public void trace(final String message, Object... args) {
        this.log.trace(message, args);
    }

    @Override
    public void debug(final String message, Object... args) {
        this.log.debug(message, args);
    }

    @Override
    public void info(final String message, Object... args) {
        this.log.info(message, args);
    }

    @Override
    public void warn(final String message, Object... args) {
        this.log.warn(message, args);
    }

    @Override
    public void error(final Throwable ex, final String message, Object... args) {
        if (null != ex) {
            this.log.error(ex.getMessage(), ex);
        } else {
            this.log.error(message, args);
        }

    }
}
