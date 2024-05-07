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

    /**
     * 代理运行所需的额外信息
     */
    public static interface IProxyAdditionalInfo {
        /**
         * 根据类型和key获取数据
         *
         * @param clazz 所需的数据类型
         * @param key   数据key
         * @param <T>
         * @return 数据
         */
        <T> T getInfoAs(Class<T> clazz, String key);
    }

    /**
     * 不安全的代理
     *
     * @param <R>        返回值
     * @param <P>        参数
     * @param function   代理方法
     * @param param      参数
     * @param additional 代理运行所需的额外信息
     * @return 代理后的结果
     * @throws Throwable 异常
     */
    <R, P> Optional<R> proxy(FunctionWithThrows<P, R, Throwable> function, P param, IProxyAdditionalInfo additional)
        throws Throwable;

    /**
     * 安全代理，将异常内部处理
     *
     * @param <R>
     * @param <P>
     * @param function  需要代理的方法
     * @param param     function参数
     * @param addtional 代理运行所需的额外信息
     * @return 运行结果
     */
    default <R, P> Optional<R> safeProxy(final FunctionWithThrows<P, R, Throwable> function, final P param,
        IProxyAdditionalInfo addtional) {
        try {
            return this.proxy(function, param, addtional);
        } catch (final Throwable e) {
            LogUtil.getInstance().getLogger(this.getClass().getName()).error(e, e.getMessage());
            return Optional.empty();
        }
    }
}
