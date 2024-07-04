package tbs.framework.utils;

import tbs.framework.base.interfaces.IChain;

/**
 * 链式调用工具
 *
 * @author Abstergo
 */

public enum ChainUtil {
    ;

    /**
     * 处理责任链
     *
     * @param chain 责任链头
     * @param param 运行参数
     * @param <P>
     * @param <R>
     * @return 最后完成的责任链节点
     */
    public static <P, R> IChain<P, R> processForChain(IChain<P, R> chain, P param) {
        R r = null;
        while (null != chain) {
            chain.doChain(param);
            if (chain.isAvailable()) {
                r = chain.getResult();
                break;
            }
            if (chain.hasNext()) {
                chain = chain.next();
            } else {
                break;
            }

        }
        if (null == chain) {
            throw new NullPointerException("chain node is null");
        }
        return chain;
    }

    /**
     * 处理责任链
     *
     * @param chain 责任链头
     * @param param 运行参数
     * @param <P>
     * @param <R>
     * @return 责任链最终处理结果
     */
    public static <P, R> R process(IChain<P, R> chain, P param) {
        return ChainUtil.processForChain(chain, param).getResult();
    }

}
