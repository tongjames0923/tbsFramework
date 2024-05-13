package tbs.framework.mq.consumer.manager;

import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

import java.util.List;
import java.util.Set;

/**
 * @author abstergo
 */
public interface IMessageConsumerManager {

    boolean match(String topic, Set<String> acceptTopics);

    /**
     * 获取全部消费者
     *
     * @return
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
     * @param messageConsumer
     * @return
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
     * 根据消息选择消息消费者
     *
     * @param messageConsumer
     * @return
     */
    List<IMessageConsumer> selectMessageConsumer(IMessage message);
}
