package tbs.framework.base.proxy;

import tbs.framework.base.intefaces.FunctionWithThrows;
import tbs.framework.base.utils.LogUtil;

import java.util.Optional;

/**
 * 代理处理接口
 *
 * @author abstergo
 */
public interface IProxy {

    public static interface IProxyAddtionalInfo {
        <T> T getInfoAs(Class<T> clazz, String key);
    }


    /**
     * 不安全的代理
     *
     * @param <R>        返回值
     * @param <P>        参数
     * @param function   代理方法
     * @param param      参数
     * @param additional
     * @return 代理后的结果
     * @throws Throwable 异常
     */
    <R, P> Optional<R> proxy(FunctionWithThrows<P, R, Throwable> function, P param, IProxyAddtionalInfo additional)
        throws Throwable;

    /**
     * 安全代理，将异常内部处理
     *
     * @param <R>
     * @param <P>
     * @param function
     * @param param
     * @param addtional
     * @return
     */
    default <R, P> Optional<R> safeProxy(final FunctionWithThrows<P, R, Throwable> function, final P param,
        IProxyAddtionalInfo addtional) {
        try {
            return this.proxy(function, param, addtional);
        } catch (final Throwable e) {
            LogUtil.getInstance().getLogger(this.getClass().getName()).error(e, e.getMessage());
            return Optional.empty();
        }
    }
}
