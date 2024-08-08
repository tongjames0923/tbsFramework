package tbs.framework.base.utils

import tbs.framework.log.ILogger

/**
 * @author abstergo
 */
abstract class LogFactory {
    protected constructor() {
        if (instance != null) {
            throw RuntimeException("Use LogFactory.getInstance() to get the unique instance of LogFactory")
        }
        instance = this
    }
    /**
     * 获取日志器
     *
     * @param name 日志器名，理论上应唯一
     * @return 日志器
     */
    abstract fun getLogger(name: String? = null): ILogger

    companion object {
        /**
         *
         */
        @JvmStatic
        var instance: LogFactory? = null
            private set
    }
}
