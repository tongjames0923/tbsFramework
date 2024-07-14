package tbs.framework.mq.center.interfaces;

import cn.hutool.core.collection.CollUtil;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;

import java.util.List;
import java.util.Optional;

/**
 * @author abstergo
 */
public interface IMessageCenterConsumeSupport {
    /**
     * 获取消息消费者管理器。
     *
     * @return 消息消费者管理器。
     */
    public Optional<IMessageConsumerManager> getMessageConsumerManager();

    /**
     * 获取消息中心中的所有消费者。
     *
     * @return 消息消费者列表。
     */
    default List<IMessageConsumer> allConsumersInCenter() {
        return getMessageConsumerManager().map(IMessageConsumerManager::getConsumers).orElse(CollUtil.newArrayList());
    }

    /**
     * 添加消息消费者。
     *
     * @param messageConsumer 消息消费者。
     * @return 当前消息中心对象。
     */
    default void appendConsumer(IMessageConsumer messageConsumer) {
        getMessageConsumerManager().ifPresent(c -> {
            c.setMessageConsumer(messageConsumer);
        });
    }

    /**
     * 移除消息消费者。
     *
     * @param consumer 消息消费者。
     * @return 当前消息中心对象。
     */
    default void remove(IMessageConsumer consumer) {
        getMessageConsumerManager().ifPresent(c -> {
            c.removeMessageConsumer(consumer);
        });
    }
}
