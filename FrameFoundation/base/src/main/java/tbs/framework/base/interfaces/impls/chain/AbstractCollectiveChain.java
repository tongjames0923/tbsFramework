package tbs.framework.base.interfaces.impls.chain;

import java.util.LinkedList;
import java.util.List;

/**
 * 可收集过去结果的责任链
 *
 * @author abstergo
 */
public abstract class AbstractCollectiveChain<P, R> extends AbstractTracbackChain<P, R> {

    /**
     * 获取当前责任链先前处理过的所有结果
     *
     * @return
     */
    public List<R> collectFromChain() {
        List<R> collectiveChain = new LinkedList<>();
        AbstractTracbackChain<P, R> chain = this;
        while (null != chain) {
            collectiveChain.add(chain.getResult());
            if (chain.hasPreviousChain()) {
                chain = chain.previous();
            } else {
                chain = null;
            }
        }
        return collectiveChain;
    }
}
