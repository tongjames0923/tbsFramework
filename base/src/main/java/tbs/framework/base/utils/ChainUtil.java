package tbs.framework.base.utils;

import tbs.framework.base.intefaces.IChain;

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
            chain = chain.next();
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
