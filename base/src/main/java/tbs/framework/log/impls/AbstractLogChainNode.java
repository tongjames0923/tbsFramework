package tbs.framework.log.impls;

import tbs.framework.base.intefaces.impls.chain.AbstractChain;

/**
 * @author abstergo
 */
public abstract class AbstractLogChainNode extends AbstractChain {

    protected abstract void log(ChainLogger.LogArg arg);

    @Override
    public void doChain(Object param) {
        if (param instanceof ChainLogger.LogArg) {
            log((ChainLogger.LogArg)param);
        }
        if (!hasNext()) {
            this.setAvailable(true);
        }
    }
}
