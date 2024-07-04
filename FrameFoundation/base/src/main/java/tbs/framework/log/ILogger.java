package tbs.framework.log;

/**
 * 日志记录
 * @author Abstergo
 */
public interface ILogger {

    /**
     * 提供日志名
     * @return 日志名称
     */
    String getName();

    void trace(String message, Object... args);

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(Throwable ex, String message, Object... args);
}
