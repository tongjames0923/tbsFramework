package tbs.framework.mq;

import java.util.List;

/**
 * @author abstergo
 */
public interface IMessageConsumerManager {
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
