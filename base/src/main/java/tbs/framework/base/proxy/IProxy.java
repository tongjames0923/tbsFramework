package tbs.framework.base.proxy;

import tbs.framework.base.intefaces.FunctionWithThrows;

import java.util.Optional;

/**
 * 代理处理接口
 *
 * @author abstergo
 */
public interface IProxy {
    /**
     * 不安全的代理
     *
     * @param function 代理方法
     * @param param    参数
     * @param <R>      返回值
     * @param <P>      参数
     * @return 代理后的结果
     * @throws Throwable 异常
     */
    <R, P> Optional<R> proxy(FunctionWithThrows<P, R, Throwable> function, P param) throws Throwable;

    /**
     * 安全代理，将异常内部处理
     *
     * @param function
     * @param param
     * @param <R>
     * @param <P>
     * @return
     */
    default <R, P> Optional<R> safeProxy(FunctionWithThrows<P, R, Throwable> function, P param) {
        try {
            return proxy(function, param);
        } catch (Throwable e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
