package tbs.framework.base.utils;

import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.log.ILogger;

import javax.annotation.Resource;


public class LogUtil {

    @Resource(name = BeanNameConstant.BUILTIN_LOGGER)
    private ILogProvider logProvider;

    private static LogUtil instance = null;

    public LogUtil() {
        instance = this;
    }

    public static ILogger getLogger(String name) {
        if (instance == null) {
            throw new IllegalStateException("LogUtil has not been initialized");
        }
        return instance.logProvider.getLogger(name);
    }

}
