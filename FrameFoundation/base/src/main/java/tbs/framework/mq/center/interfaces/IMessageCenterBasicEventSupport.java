package tbs.framework.mq.center.interfaces;

import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.receiver.IMessageReceiver;

/**
 * 定义消息中心的基本事件支持接口。
 * <p>
 * 消息中心在处理消息时，可能会触发一些事件，如消息到达、消息发送等。 实现此接口可以提供一种机制，以便在消息中心处理这些事件时通知到监听这些事件的对象。
 * </p>
 *
 * @author abstergo
 * @see IMessageConnector
 * @see IMessage
 * @see IMessageReceiver
 * @since 1.0.0
 */
public interface IMessageCenterBasicEventSupport {

    /**
     * 消息到达事件。
     * <p>
     * 当消息到达消息中心，并通过连接器传递给接收器时，会触发此事件。
     * </p>
     *
     * @param msg       消息对象。
     * @param connector 消息连接器。
     * @param receiver  消息接收器。
     */
    public void messageArrived(IMessage msg, IMessageConnector connector, IMessageReceiver receiver);

    /**
     * 消息发送事件。
     * <p>
     * 当消息从消息中心发送出去时，会触发此事件。
     * </p>
     *
     * @param msg 消息对象。
     */
    public void messageSent(IMessage msg);

}
