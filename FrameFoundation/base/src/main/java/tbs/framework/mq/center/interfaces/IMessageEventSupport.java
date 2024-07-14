package tbs.framework.mq.center.interfaces;

import tbs.framework.mq.event.IMessageQueueEvents;

import java.util.Optional;

/**
 * @author abstergo
 */
public interface IMessageEventSupport {
    /**
     * 获取消息队列事件处理器。
     *
     * @return 消息队列事件处理器。
     */
    public Optional<IMessageQueueEvents> getMessageQueueEvents();

}
