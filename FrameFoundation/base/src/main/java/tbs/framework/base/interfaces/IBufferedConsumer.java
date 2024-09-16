package tbs.framework.base.interfaces;

import java.util.function.Consumer;

/**
 * 缓冲区消费者
 *
 * @param <T>
 */
public interface IBufferedConsumer<T> extends Consumer<T> {

    /**
     * 添加待处理的数据进缓存区
     *
     * @param t
     */
    public void add(T t);

    /**
     * 清空缓冲区
     */
    public void flush();

    /**
     * 获取缓冲区大小
     *
     * @return
     */
    public long getBufferSize();

    /**
     * 获取缓冲区中待处理的数据数量
     *
     * @return
     */
    public long getBufferedCount();

}
