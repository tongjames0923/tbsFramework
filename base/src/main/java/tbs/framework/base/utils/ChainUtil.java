package tbs.framework.base.utils;

import tbs.framework.base.intefaces.IChain;

public class ChainUtil {

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

    public static <P, R> R process(IChain<P, R> chain, P param) {
        return processForChain(chain, param).getResult();
    }

}
