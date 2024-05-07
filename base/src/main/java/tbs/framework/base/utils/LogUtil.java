package tbs.framework.base.utils;

import tbs.framework.base.log.ILogger;

public abstract class LogUtil {

    private static LogUtil logUtil = null;

    public static LogUtil getInstance() {
        return LogUtil.logUtil;
    }

    protected LogUtil() {
        if (null == LogUtil.logUtil) {
            LogUtil.logUtil = this;
        }
    }

    public abstract ILogger getLogger(final String name);

}
