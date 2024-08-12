package tbs.framework.sql.interfaces;

import tbs.framework.base.interfaces.impls.chain.AbstractChain;

/**
 * 抽象转换器类，用于实现将对象转换为字符串的功能。
 * <p>
 * 该类继承自{@link AbstractChain}，实现了{@link AbstractConvertor#doChain(Object)}方法， 用于处理传入的对象，并将其转换为字符串。
 *
 * @param <T> 转换器支持的类型
 * @author abstergo
 */
public abstract class AbstractConvertor extends AbstractChain<Object, String> {

    /**
     * 判断转换器是否支持给定的类型
     *
     * @param t 要判断的类型
     * @return 如果支持，则返回true，否则返回false
     */
    protected abstract boolean support(Class<?> t);

    /**
     * 将给定的对象转换为字符串
     *
     * @param value 要转换的对象
     * @return 转换后的字符串
     */
    protected abstract String doConvert(Object value);

    /**
     * 处理传入的对象，并将其转换为字符串
     *
     * @param param 传入的对象
     */
    @Override
    public void doChain(Object param) {
        if (support(param == null ? null : param.getClass())) {
            done(doConvert(param));
        }
    }
}
