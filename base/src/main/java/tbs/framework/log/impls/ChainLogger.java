package tbs.framework.log.impls;

import lombok.AllArgsConstructor;
import lombok.Data;
import tbs.framework.base.intefaces.IChainProvider;
import tbs.framework.log.ILogger;
import tbs.framework.utils.ChainUtil;

/**
 * @author abstergo
 */
public class ChainLogger implements ILogger {


    IChainProvider loggerChainProvider;

    private String name;

    @Data
    @AllArgsConstructor
    public static class LogArg {
        private String loggerName;
        private String message;
        private Object[] args;

        public static enum Level {
            TRACE, DEBUG, INFO, WARN, ERROR
        }

        private Level level;
    }

    public ChainLogger(IChainProvider loggerChainProvider, String name) {
        this.loggerChainProvider = loggerChainProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void trace(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.TRACE));
    }

    @Override
    public void debug(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.DEBUG));

    }

    @Override
    public void info(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.INFO));

    }

    @Override
    public void warn(String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.WARN));

    }

    @Override
    public void error(Throwable ex, String message, Object... args) {
        ChainUtil.processForChain(loggerChainProvider.beginChain(),
            new LogArg(getName(), message, args, LogArg.Level.ERROR));
    }
}
