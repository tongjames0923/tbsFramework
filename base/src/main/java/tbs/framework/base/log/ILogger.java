package tbs.framework.base.log;

public interface ILogger {

    String getName();

    void trace(String message);

    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(Throwable ex, String message);
}
