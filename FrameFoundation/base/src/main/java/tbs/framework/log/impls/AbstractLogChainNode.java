package tbs.framework.log.impls;

import tbs.framework.base.interfaces.impls.chain.AbstractChain;

/**
 * The type Abstract log chain node.
 *
 * @author abstergo
 */
public abstract class AbstractLogChainNode extends AbstractChain<ChainLogger.LogArg, Void> {

    /**
     * 日志逻辑
     *
     * @param arg the arg
     */
    protected abstract void log(ChainLogger.LogArg arg);

    @Override
    public void doChain(ChainLogger.LogArg param) {
        log((ChainLogger.LogArg)param);
        if (!hasNext()) {
            this.setAvailable(true);
        }
    }
}
