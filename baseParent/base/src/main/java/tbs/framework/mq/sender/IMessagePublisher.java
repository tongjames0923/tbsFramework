package tbs.framework.mq.sender;

import tbs.framework.mq.message.IMessage;

/**
 * @author abstergo
 */
public interface IMessagePublisher {


    /**
     * 消息批量发布
     * @param message 信息
     */
    void publishAll(IMessage... message);
}
