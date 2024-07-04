package tbs.framework.mq.consumer;

import tbs.framework.mq.message.IMessage;

import java.util.Set;

/**
 * @author Abstergo
 */
public interface IMessageConsumer {

    /**
     * 消费者id
     *
     * @return
     */
    String consumerId();

    /**
     * 支持的话题
     *
     * @return
     */

    Set<String> avaliableTopics();

    /**
     * 消费方法
     *
     * @param message
     */

    void consume(IMessage message);

}
