package tbs.framework.base.intefaces.impls.chain;

import tbs.framework.base.intefaces.IChain;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 抽象责任链 提供更方便的调用
 *
 * @author abstergo
 * @param <P>
 * @param <R>
 */
public abstract class AbstractChain<P, R> implements IChain<P, R> {
    private AbstractChain<P, R> next = null;
    private boolean isAvailable = false;
    private R result;

    protected R onChangeResult(R result) {

        return result;
    }

    protected AbstractChain<P, R> onNextBefore(AbstractChain<P, R> nxt) {
        return nxt;
    }

    /**
     * 产生一个新的创建者
     *
     * @return
     */
    public static Builder newChain() {
        return new Builder();
    }

    /**
     * 设置结果
     *
     * @param result
     */
    protected void setResult(R result) {
        this.result = result;
        onChangeResult(result);
    }

    /**
     * 设置是否结束责任链
     *
     * @param available
     */
    protected void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    /**
     * 责任链创建器
     *
     */
    public static class Builder<P, R, TR extends AbstractChain<P, R>> {
        private Queue<TR> queue = new LinkedList<>();

        public Builder<P, R, TR> add(final TR chain) {
            if (null != chain) {
                queue.add(chain);
            }
            return this;
        }

        public TR build() {
            TR head = null, tail = null;
            while (!queue.isEmpty()) {
                final TR current = queue.poll();
                if (null == head) {
                    head = current; // 第一个节点将成为链的头部
                } else {
                    tail.setNext(current);  // 将当前节点链接到链的末尾
                }
                tail = current; // 更新链的末尾为当前节点
            }
            return head; // 返回链的头部节点
        }
    }

    /**
     * 责任链完成回调,整个责任链将中断至此
     *
     * @param result
     */
    public void done(R result) {
        setResult(result);
        setAvailable(true);
    }

    @Override
    public R getResult() {
        return result;
    }

    @Override
    public IChain<P, R> next() {
        next = onNextBefore(next);
        return next;
    }

    protected void setNext(AbstractChain<P, R> next) {
        this.next = next;
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

}
