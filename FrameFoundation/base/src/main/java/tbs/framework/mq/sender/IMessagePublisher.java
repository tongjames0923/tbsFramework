package tbs.framework.mq.sender;

import org.jetbrains.annotations.NotNull;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.message.IMessage;

/**
 * @author abstergo
 */
public interface IMessagePublisher {

    /**
     * 消息发布
     *
     * @param message 信息
     * @param center 消息中心
     */
    void publish(@NotNull IMessage message,@NotNull AbstractMessageCenter center);
}
