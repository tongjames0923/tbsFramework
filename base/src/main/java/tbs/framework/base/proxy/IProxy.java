package tbs.framework.base.proxy;

import java.util.Optional;
import java.util.function.Function;

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
     * @throws Exception 异常
     */
    <R, P> Optional<R> proxy(Function<P, R> function, P param) throws Exception;

    /**
     * 安全代理，将异常内部处理
     *
     * @param function
     * @param param
     * @param <R>
     * @param <P>
     * @return
     */
    default <R, P> Optional<R> safeProxy(Function<P, R> function, P param) {
        try {
            return proxy(function, param);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
