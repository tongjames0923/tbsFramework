package tbs.framework.base.proxy.impls;

import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;

import javax.annotation.Resource;
import java.util.function.Function;

public class LogExceptionProxy implements IProxy {
    @Resource(name = BeanNameConstant.BUILTIN_LOGGER)
    ILogger logger;


    @Override
    public <R, P> R proxy(Function<P, R> function, P param) {
        try {
            return function.apply(param);
        } catch (RuntimeException ex) {
            logger.error(ex, "proxy error");
        }
        return null;
    }
}
