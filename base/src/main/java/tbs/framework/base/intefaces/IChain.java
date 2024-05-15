package tbs.framework.base.intefaces;

/**
 * 责任链接口
 *
 * @param <P> 入参
 * @param <R> 返回值
 */
public interface IChain<P, R> {
    /**
     * 下一个责任链节点
     *
     * @return
     */
    IChain<P, R> next();

    boolean hasNext();

    /**
     * 当前责任链是否已产生结果，若产生结果则不继续执行责任链
     *
     * @return true 已产生，false未产生结果
     */
    boolean isAvailable();

    /**
     * 获取当前节点的结果
     *
     * @return
     */
    R getResult();

    /**
     * 执行当前责任链节点
     *
     * @param param
     */
    void doChain(P param);
}
