package tbs.framework.base.interfaces;

import tbs.framework.base.interfaces.impls.chain.AbstractChain;

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
