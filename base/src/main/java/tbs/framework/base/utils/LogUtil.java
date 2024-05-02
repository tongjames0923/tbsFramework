package tbs.framework.base.utils;

import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.log.ILogger;

import javax.annotation.Resource;


public class LogUtil {

    @Resource(name = BeanNameConstant.BUILTIN_LOGGER)
    private ILogProvider logProvider;

    private static LogUtil logUtil = null;

    public static LogUtil getInstance() {
        return logUtil;
    }

    public LogUtil() {
        if (logUtil == null) {
            LogUtil.logUtil = this;
        }
    }

    public ILogger getLogger(final String name) {
        return this.logProvider.getLogger(name);
    }

}
