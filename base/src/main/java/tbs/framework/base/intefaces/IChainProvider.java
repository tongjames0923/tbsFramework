package tbs.framework.base.intefaces;

import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * 责任链提供接口
 */
public interface IChainProvider {
    /**
     * 产生责任链
     *
     * @return
     */
    AbstractChain beginChain();
}
