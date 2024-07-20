package tbs.framework.log;

/**
 * 日志记录接口
 *
 * <p>本接口定义了日志记录的基本操作，包括获取日志名称、记录不同级别的日志信息（跟踪、调试、信息、警告、错误）等。
 *
 * @author Abstergo
 * @since 1.0
 */
public interface ILogger {

    /**
     * 获取日志名称。
     *
     * <p>此方法用于获取当前日志记录器的名称。
     *
     * @return 日志名称
     */
    String getName();

    /**
     * 记录跟踪级别的日志信息。
     *
     * <p>此方法用于记录跟踪级别的日志信息，通常用于开发和调试过程中。
     *
     * @param message 日志信息
     * @param args    变量参数
     */
    void trace(String message, Object... args);

    /**
     * 记录调试级别的日志信息。
     *
     * <p>此方法用于记录调试级别的日志信息，通常用于开发和调试过程中。
     *
     * @param message 日志信息
     * @param args    变量参数
     */
    void debug(String message, Object... args);

    /**
     * 记录信息级别的日志信息。
     *
     * <p>此方法用于记录信息级别的日志信息，用于正常运行时的日志记录。
     *
     * @param message 日志信息
     * @param args    变量参数
     */
    void info(String message, Object... args);

    /**
     * 记录警告级别的日志信息。
     *
     * <p>此方法用于记录警告级别的日志信息，表示有潜在的问题需要关注。
     *
     * @param message 日志信息
     * @param args    变量参数
     */
    void warn(String message, Object... args);

    /**
     * 记录错误级别的日志信息。
     *
     * <p>此方法用于记录错误级别的日志信息，表示程序运行过程中出现了错误。
     *
     * @param ex      异常对象
     * @param message 日志信息
     * @param args    变量参数
     */
    void error(Throwable ex, String message, Object... args);
}
