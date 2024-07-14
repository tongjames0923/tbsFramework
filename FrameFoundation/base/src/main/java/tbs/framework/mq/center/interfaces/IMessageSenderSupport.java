package tbs.framework.mq.center.interfaces;

import tbs.framework.mq.sender.IMessagePublisher;

/**
 * @author abstergo
 */
public interface IMessageSenderSupport {

    /**
     * 获取消息发布器。
     *
     * @return 消息发布器。
     */
    public IMessagePublisher getMessagePublisher();

    /**
     * 设置消息发布器。
     *
     * @param messagePublisher 消息发布器。
     */
    public void setMessagePublisher(IMessagePublisher messagePublisher);

}
