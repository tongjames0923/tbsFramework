package tbs.framework.auth.interfaces;

public interface IErrorHandler {
    Object handleError(Throwable ex, Class<?> returnType, Object result);
}
