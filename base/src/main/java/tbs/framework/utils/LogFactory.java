package tbs.framework.utils;

import tbs.framework.log.ILogger;

/**
 * @author abstergo
 */
public abstract class LogFactory {

    /**
     *
     */
    private static LogFactory logFactory = null;

    /**
     *
     */
    public static LogFactory getInstance() {
        return LogFactory.logFactory;
    }

    /**
     *
     */
    protected LogFactory() {
    }

    public static void setLogFactory(LogFactory logFactory) {
        LogFactory.logFactory = logFactory;
    }

    /**
     * 获取日志器
     *
     * @param name 日志器名，理论上应唯一
     * @return 日志器
     */
    public abstract ILogger getLogger(final String name);

}
