package tbs.framework.base.proxy.impls;

import cn.hutool.core.lang.UUID;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.utils.LogUtil;

import java.util.Optional;
import java.util.function.Function;

public class LogExceptionProxy implements IProxy {

    private final ILogger logger;

    public LogExceptionProxy(LogUtil util) {
        logger = util.getLogger(LogExceptionProxy.class.getName());
    }

    @Override
    public <R, P> Optional<R> proxy(Function<P, R> function, P param) {
        String uuid = UUID.randomUUID().toString();
        logger.info("Proxying [" + uuid + "]");
        Optional<R> result = Optional.empty();
        try {
            result = Optional.ofNullable(function.apply(param));
            logger.info(String.format("Proxying [%s] result returned", uuid));
        } catch (Exception ex) {
            logger.error(ex, String.format("Proxying [%s] failed.message:[%s]", uuid, ex.getMessage()));
        }
        return result;
    }
}
