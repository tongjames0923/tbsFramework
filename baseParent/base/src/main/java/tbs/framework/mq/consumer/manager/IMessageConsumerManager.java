package tbs.framework.mq.consumer.manager;

import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

import java.util.List;
import java.util.Set;

/**
 * The interface Message consumer manager.
 *
 * @author abstergo
 */
public interface IMessageConsumerManager {

    /**
     * 根据管理器的匹配机制进行匹配
     *
     * @param topic        the topic
     * @param acceptTopics the accept topics
     * @return true 匹配，false 不匹配
     */
    boolean match(String topic, Set<String> acceptTopics);

    /**
     * 获取全部消费者
     *
     * @return consumers
     */
    List<IMessageConsumer> getConsumers();

    /**
     * 消费消息
     *
     * @param center   消费的消息中心
     * @param consumer 消费者
     * @param message  消息
     */
    default void consumeOnce(AbstractMessageCenter center, IMessageConsumer consumer, IMessage message) {
        while (true) {
            int r = 0;
            try {
                consumer.consume(message);
                break;
            } catch (Exception e) {
                if (!center.errorOnConsume(message, r++, e, consumer)) {
                    break;
                }
            }
        }
    }

    /**
     * 导入消息消费器
     *
     * @param messageConsumer the message consumer
     * @return 当前实体
     */
    IMessageConsumerManager setMessageConsumer(IMessageConsumer messageConsumer);

    /**
     * 移除消息消费器
     *
     * @param messageConsumer 目标消息消费器
     * @return true 移除成功 ，false移除失败
     */
    boolean removeMessageConsumer(IMessageConsumer messageConsumer);

    /**
     * 根据消息进行匹配，选择消息消费者
     *
     * @param message the message
     * @return list
     */
    List<IMessageConsumer> selectMessageConsumer(IMessage message);
}
