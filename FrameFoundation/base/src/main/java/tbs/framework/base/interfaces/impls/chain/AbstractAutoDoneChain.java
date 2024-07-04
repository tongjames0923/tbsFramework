package tbs.framework.base.interfaces.impls.chain;

/**
 * @author abstergo
 */
public abstract class AbstractAutoDoneChain<P, R> extends AbstractChain<P, R> {

    /**
     * 执行完后判断是否完成责任链
     *
     * @return true:直接完成 false:继续执行
     */
    protected abstract boolean conditionForDone();

    /**
     * 责任链功能运行
     *
     * @param param
     */
    protected abstract void run(P param);

    @Override
    public void doChain(P param) {
        run(param);
        if (conditionForDone()) {
            setAvailable(true);
        }
    }
}
