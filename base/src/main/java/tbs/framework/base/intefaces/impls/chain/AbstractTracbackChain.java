package tbs.framework.base.intefaces.impls.chain;

/**
 * 可回溯责任链
 *
 * @author abstergo
 */
public abstract class AbstractTracbackChain<P, R> extends AbstractChain<P, R> {
    private AbstractTracbackChain<P, R> previousChain = null;

    /**
     * 上一个节点
     *
     * @return
     */
    public AbstractTracbackChain<P, R> previous() {
        return previousChain;
    }

    /**
     * 是否存在上一个节点
     *
     * @return
     */
    public boolean hasPreviousChain() {
        return null != previousChain;
    }

    /**
     * 单纯设置值
     *
     * @param res
     */
    public void set(R res) {
        this.setResult(res);
    }

}
