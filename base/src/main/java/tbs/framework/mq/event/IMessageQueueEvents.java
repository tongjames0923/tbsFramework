package tbs.framework.mq.event;

import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;

public interface IMessageQueueEvents {
    /**
     * 当成功获取消息时
     *
     * @param message 接收到的消息
     */
    void onMessageReceived(IMessage message);

    /**
     * 当消息成功发送
     *
     * @param message
     */
    void onMessageSent(IMessage message);

    /**
     * 当消息处理失败
     *
     * @param message   失败的消息
     * @param retryed   已重试的次数
     * @param type      处理方式
     * @param consumer  出错的消费者
     * @param throwable 异常
     * @return 是否重试
     */
    boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type, Throwable throwable,
        IMessageConsumer consumer);

    public static enum MessageHandleType {
        /**
         * 发送事件
         */
        Send,
        /**
         * 接收事件
         */
        Receive,
        /**
         * 消费事件
         */
        Consume
    }
}
