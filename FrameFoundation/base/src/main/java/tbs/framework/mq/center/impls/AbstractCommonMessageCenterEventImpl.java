package tbs.framework.mq.center.impls;

import tbs.framework.mq.center.interfaces.IMessageCenterBasicEventSupport;
import tbs.framework.mq.center.interfaces.IMessageCenterErrorHandleSupport;
import tbs.framework.mq.center.interfaces.IMessageEventSupport;
import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;

/**
 * @author abstergo
 */
public abstract class AbstractCommonMessageCenterEventImpl
    implements IMessageEventSupport, IMessageCenterErrorHandleSupport, IMessageCenterBasicEventSupport {

    /**
     * 消息异常处理回调。
     *
     * @param msg      异常消息。
     * @param r        重试次数。
     * @param type     操作类型。
     * @param e        异常。
     * @param consumer 异常消费者。
     * @return 是否重试。
     */
    @Override
    public boolean handleMessageError(IMessage msg, int r, IMessageQueueEvents.MessageHandleType type, Throwable e,
        IMessageConsumer consumer) {
        return getMessageQueueEvents().map((m) -> {
            return m.onMessageFailed(msg, r, type, e, consumer);
        }).orElse(false);
    }

    /**
     * 消费时异常处理。
     *
     * @param m 异常消息。
     * @param r 重试次数。
     * @param e 异常。
     * @param c 异常消费者。
     * @return 是否重试。
     */
    @Override
    public boolean errorOnConsume(IMessage m, int r, Throwable e, IMessageConsumer c) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Consume, e, c);
    }

    /**
     * 接收时异常处理。
     *
     * @param r 重试次数。
     * @param e 异常。
     * @return 是否重试。
     */
    @Override
    public boolean errorOnReceive(Throwable e, int r) {
        return handleMessageError(null, r, IMessageQueueEvents.MessageHandleType.Receive, e, null);
    }

    /**
     * 发送时异常处理。
     *
     * @param m 异常的消息。
     * @param r 重试次数。
     * @param e 异常。
     * @return 是否重试。
     */
    @Override
    public boolean errorOnSend(IMessage m, int r, Throwable e) {
        return handleMessageError(m, r, IMessageQueueEvents.MessageHandleType.Send, e, null);
    }

    /**
     * 消息抵达触发。
     *
     * @param msg       消息。
     * @param connector 消息连接器。
     * @param receiver  消息接收器。
     */
    @Override
    public void messageArrived(IMessage msg, IMessageConnector connector, IMessageReceiver receiver) {
        getMessageQueueEvents().ifPresent(c -> c.onMessageReceived(msg, connector, receiver));
    }

    /**
     * 消息已发送回调。
     *
     * @param msg 消息。
     */
    @Override
    public void messageSent(IMessage msg) {
        getMessageQueueEvents().ifPresent((ev) -> {
            ev.onMessageSent(msg);
        });
    }
}
