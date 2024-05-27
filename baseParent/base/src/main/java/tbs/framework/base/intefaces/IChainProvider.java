package tbs.framework.base.intefaces;

import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * 责任链提供接口
 * @author abstergo
 */
public interface IChainProvider<P,R> {
    /**
     * 产生责任链
     *
     * @return
     */
    AbstractChain<P,R> beginChain();
}
