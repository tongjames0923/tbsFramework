package tbs.framework.utils;

import tbs.framework.log.ILogger;

public abstract class LogUtil {

    /**
     *
     */
    private static LogUtil logUtil = null;

    /**
     *
     */
    public static LogUtil getInstance() {
        return LogUtil.logUtil;
    }

    /**
     *
     */
    protected LogUtil() {
        if (null == LogUtil.logUtil) {
            LogUtil.logUtil = this;
        }
    }

    /**
     * 获取日志器
     *
     * @param name 日志器名，理论上应唯一
     * @return 日志器
     */
    public abstract ILogger getLogger(final String name);

}
