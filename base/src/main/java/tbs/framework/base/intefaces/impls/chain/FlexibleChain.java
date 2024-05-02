package tbs.framework.base.intefaces.impls.chain;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 *
 * @author abstergo
 */
public class FlexibleChain<P, R> extends AbstractChain<P, R> {

    private BiConsumer<P, FlexibleChain<P, R>> workElement;
    private BiFunction<AbstractChain<P, R>, FlexibleChain<P, R>, AbstractChain<P, R>> onNextEvent;
    private BiFunction<R, FlexibleChain<P, R>, R> onResultChangedEvent;

    public BiConsumer<P, FlexibleChain<P, R>> workElement() {
        return this.workElement;
    }

    /**
     * 设置责任链工作方法
     *
     * @param workElement P 入参，FlexibleChain<P, R> 调用的本身对象
     * @return
     */
    public FlexibleChain<P, R> setWorkElement(final BiConsumer<P, FlexibleChain<P, R>> workElement) {
        this.workElement = workElement;
        return this;
    }

    public BiFunction<AbstractChain<P, R>, FlexibleChain<P, R>, AbstractChain<P, R>> onNextEvent() {
        return this.onNextEvent;
    }

    /**
     * 设置当执行下一个节点时触发的事件
     *
     * @param onNextEvent AbstractChain<P, R> 预选下一个节点的对象 ，FlexibleChain<P, R>调用对象本身 ，AbstractChain<P, R>最终下一个节点的对象
     * @return
     */
    public FlexibleChain<P, R> setOnNextEvent(
        final BiFunction<AbstractChain<P, R>, FlexibleChain<P, R>, AbstractChain<P, R>> onNextEvent) {
        this.onNextEvent = onNextEvent;
        return this;
    }

    public BiFunction<R, FlexibleChain<P, R>, R> onResultChangedEvent() {
        return this.onResultChangedEvent;
    }

    /**
     * 设置当结果被赋值时的事件
     * @param onResultChangedEvent R 预选的入参结果， FlexibleChain<P, R> 调用者本身 ，最终结果
     * @return
     */
    public FlexibleChain<P, R> setOnResultChangedEvent(
        final BiFunction<R, FlexibleChain<P, R>, R> onResultChangedEvent) {
        this.onResultChangedEvent = onResultChangedEvent;
        return this;
    }

    @Override
    protected AbstractChain<P, R> onNextBefore(AbstractChain<P, R> nxt) {
        if (null == this.onNextEvent) {
            return super.onNextBefore(nxt);
        } else {
            return onNextEvent.apply(nxt, this);
        }
    }

    @Override
    protected R onChangeResult(R result) {
        if (null == this.onResultChangedEvent) {
            return super.onChangeResult(result);
        } else {
            return onResultChangedEvent.apply(result, this);
        }
    }

    @Override
    public void doChain(P param) {
        if (null != this.workElement) {
            workElement.accept(param, this);
        }
    }
}
