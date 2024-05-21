package tbs.framework.log.impls;

import lombok.AllArgsConstructor;
import lombok.Data;
import tbs.framework.log.AbstractLogChainProvider;
import tbs.framework.log.ILogger;
import tbs.framework.utils.ChainUtil;

/**
 * The type Chain logger.
 *
 * @author abstergo
 */
public class ChainLogger implements ILogger {

    /**
     * 记录日志工作链提供器
     */
    AbstractLogChainProvider loggerChainProvider;

    private String name;

    /**
     * The type Log arg.
     */
    @Data
    @AllArgsConstructor
    public static class LogArg {

        /**
         * 日志器名
         */
        private String loggerName;

        /**
         * 消息
         */
        private String message;

        /**
         * 消息参数
         */

        private Object[] args;

        /**
         * 日志级别
         */
        private Level level;

        /**
         * 当错误时可提供的异常
         */
        private Throwable error;

        /**
         * The enum Level.
         */
        public static enum Level {
            /**
             * Trace level.
             */
            TRACE,
            /**
             * Debug level.
             */
            DEBUG,
            /**
             * Info level.
             */
            INFO,
            /**
             * Warn level.
             */
            WARN,
            /**
             * Error level.
             */
            ERROR
        }

    }

    /**
     * Instantiates a new Chain logger.
     *
     * @param loggerChainProvider the logger chain provider
     * @param name                the name
     */
    public ChainLogger(AbstractLogChainProvider loggerChainProvider, String name) {
        this.loggerChainProvider = loggerChainProvider;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void trace(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.TRACE, null));
    }

    @Override
    public void debug(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.DEBUG, null));

    }

    @Override
    public void info(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.INFO, null));

    }

    @Override
    public void warn(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.WARN, null));

    }

    @Override
    public void error(Throwable ex, String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.ERROR, ex));
    }
}
