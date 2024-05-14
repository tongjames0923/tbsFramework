package tbs.framework.utils.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.log.ILogger;
import tbs.framework.log.impls.Slf4jLogger;
import tbs.framework.utils.BeanUtil;
import tbs.framework.utils.LogUtil;

/**
 * @author Abstergo
 */
public class Slf4jLoggerUtil extends LogUtil {

    private String loggerName(String n) {
        return "LOGGER-" + n;
    }

    @Override
    public ILogger getLogger(final String name) {
        if (SpringUtil.getApplicationContext().containsBean(name)) {
            return SpringUtil.getBean(loggerName(name), ILogger.class);
        } else {
            final ILogger logger = new Slf4jLogger(name);
            BeanUtil.registerBean(logger, loggerName(name));
            return logger;
        }
    }
}
