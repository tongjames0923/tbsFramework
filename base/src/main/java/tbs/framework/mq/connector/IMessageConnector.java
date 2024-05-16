package tbs.framework.mq.connector;

import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.receiver.IMessageReceiver;
import tbs.framework.mq.sender.IMessagePublisher;

import java.util.List;

/**
 * The interface Message connector.
 *
 * @author Abstergo
 */
public interface IMessageConnector {

    /**
     * 为消息中心生成消息发布器
     *
     * @param center the center
     */
    void createPublishers(AbstractMessageCenter center);

    /**
     * 为消息中心生成消息接收器
     *
     * @param center the center
     */
    void createReceivers(AbstractMessageCenter center);

    /**
     * 为消息中心销毁发布器
     *
     * @param center     the center
     * @param publishers 需要销毁的发布器
     */
    void destoryPublishers(AbstractMessageCenter center, List<IMessagePublisher> publishers);

    /**
     * 销毁消息接收器
     *
     * @param center    the center
     * @param receivers 需要销毁的接收器
     */
    void destoryReceivers(AbstractMessageCenter center, List<IMessageReceiver> receivers);
}
