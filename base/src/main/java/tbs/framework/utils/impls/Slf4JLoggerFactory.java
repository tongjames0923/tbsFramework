package tbs.framework.utils.impls;

import tbs.framework.log.ILogger;
import tbs.framework.log.impls.Slf4jLogger;

/**
 * @author Abstergo
 */
public class Slf4JLoggerFactory extends AbstractBeanLogFactory {
    @Override
    protected ILogger newLogger(String name) {
        return new Slf4jLogger(name);
    }
}
