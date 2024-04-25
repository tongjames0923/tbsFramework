package tbs.framework.auth.interfaces;

/**
 * 全局异常处理器
 *
 * @author abstergo
 */
public interface IErrorHandler {
    /**
     * 处理异常
     *
     * @param ex 收到的异常
     * @return 异常处理后的结果
     */
    Object handleError(Throwable ex);
}
