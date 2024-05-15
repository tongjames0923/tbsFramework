package tbs.framework.utils.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.base.intefaces.IChainProvider;
import tbs.framework.log.ILogger;
import tbs.framework.log.impls.ChainLogger;
import tbs.framework.utils.BeanUtil;
import tbs.framework.utils.LogUtil;

import javax.annotation.Resource;

/**
 * @author abstergo
 */
public class ChainLoggerUtil extends LogUtil {

    public static final String LOGGER_CHAIN = "LOGGER_CHAIN";

    @Resource(name = LOGGER_CHAIN)
    private IChainProvider logChainProvider;

    private String loggerName(String n) {
        return "LOGGER-" + n;
    }

    @Override
    public ILogger getLogger(String name) {
        if (SpringUtil.getApplicationContext().containsBean(loggerName(name))) {
            return SpringUtil.getBean(loggerName(name), ILogger.class);
        } else {
            final ILogger logger = new ChainLogger(logChainProvider, name);
            BeanUtil.registerBean(logger, loggerName(name));
            return logger;
        }
    }
}
