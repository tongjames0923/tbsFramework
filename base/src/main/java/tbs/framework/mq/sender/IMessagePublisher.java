package tbs.framework.mq.sender;

import tbs.framework.mq.message.IMessage;

/**
 * @author abstergo
 */
public interface IMessagePublisher {

    void publishAll(IMessage... message);
}
