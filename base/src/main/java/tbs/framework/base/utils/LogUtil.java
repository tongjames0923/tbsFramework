package tbs.framework.base.utils;

import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogProvider;
import tbs.framework.base.log.ILogger;

import javax.annotation.Resource;


public class LogUtil {

    @Resource(name = BeanNameConstant.BUILTIN_LOGGER)
    private ILogProvider logProvider;

    public ILogger getLogger(final String name) {
        return this.logProvider.getLogger(name);
    }

}
