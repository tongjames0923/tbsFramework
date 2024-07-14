package tbs.framework.mq.center.interfaces;

import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;

/**
 * 定义消息中心错误处理支持接口。
 * <p>
 * 当消息中心在处理消息时发生错误，可以通过实现此接口来处理这些错误。
 * </p>
 *
 * @author abstergo
 * @see IMessageConsumer
 * @see IMessageQueueEvents
 * @see IMessage
 * @since 1.0.0
 */
public interface IMessageCenterErrorHandleSupport {
    /**
     * 处理消息错误。
     * <p>
     * 当消息处理过程中发生错误时，会调用此方法。
     * </p>
     *
     * @param msg      消息对象。
     * @param r        重试次数。
     * @param type     消息处理类型。
     * @param e        错误对象。
     * @param consumer 消息消费者。
     * @return 是否成功处理错误。
     */
    boolean handleMessageError(IMessage msg, int r, IMessageQueueEvents.MessageHandleType type, Throwable e,
        IMessageConsumer consumer);

    /**
     * 消费消息时发生错误。
     * <p>
     * 当消息消费者在消费消息时发生错误时，会调用此方法。
     * </p>
     *
     * @param m 消息对象。
     * @param r 重试次数。
     * @param e 错误对象。
     * @param c 消息消费者。
     * @return 是否成功处理错误。
     */
    boolean errorOnConsume(IMessage m, int r, Throwable e, IMessageConsumer c);

    /**
     * 接收消息时发生错误。
     * <p>
     * 当消息接收器在接收消息时发生错误时，会调用此方法。
     * </p>
     *
     * @param e 错误对象。
     * @param r 重试次数。
     * @return 是否成功处理错误。
     */
    boolean errorOnReceive(Throwable e, int r);

    /**
     * 发送消息时发生错误。
     * <p>
     * 当消息中心在发送消息时发生错误时，会调用此方法。
     * </p>
     *
     * @param m 消息对象。
     * @param r 重试次数。
     * @param e 错误对象。
     * @return 是否成功处理错误。
     */
    boolean errorOnSend(IMessage m, int r, Throwable e);
}
