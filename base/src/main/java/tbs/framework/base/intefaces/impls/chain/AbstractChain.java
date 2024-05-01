package tbs.framework.base.intefaces.impls.chain;

import tbs.framework.base.intefaces.IChain;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 抽象责任链 提供更方便的调用
 *
 * @param <P>
 * @param <R>
 */
public abstract class AbstractChain<P, R> implements IChain<P, R> {
    private AbstractChain<P, R> next = null;
    private boolean isAvailable = false;
    private R result;

    /**
     * 产生一个新的创建者
     *
     * @param <P>
     * @param <R>
     * @return
     */
    public static <P, R> Builder<P, R> newChain() {
        return new Builder<>();
    }

    /**
     * 责任链创建器
     *
     * @param <P>
     * @param <R>
     */
    public static class Builder<P, R> {
        private Queue<AbstractChain<P, R>> queue = new LinkedList<>();

        public Builder<P, R> add(AbstractChain<P, R> chain) {
            if (null != chain) {
                queue.add(chain);
            }
            return this;
        }

        public AbstractChain<P, R> build() {
            AbstractChain<P, R> chain = null;
            while (!queue.isEmpty()) {
                final AbstractChain<P, R> temp = queue.poll();
                if (null == chain) {
                    chain = temp;
                } else {
                    chain.next = temp;
                }
            }
            return chain;
        }
    }

    /**
     * 责任链完成回调,整个责任链将中断至此
     *
     * @param result
     */
    public void done(R result) {
        isAvailable = true;
        this.result = result;
    }


    @Override
    public R getResult() {
        return result;
    }

    @Override
    public IChain<P, R> next() {
        return next;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

}
