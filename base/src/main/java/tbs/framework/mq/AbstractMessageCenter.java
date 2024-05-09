package tbs.framework.mq;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.NoSuchElementException;

public abstract class AbstractMessageCenter {

    public enum MessageHandleType {
        Send, Receive
    }

    protected void checkInputConsumer(IMessageConsumer messageConsumer) {
        if (messageConsumer == null || StrUtil.isEmpty(messageConsumer.consumerId())) {
            throw new NullPointerException("消费者为空或消息者id为空");
        }
        if (CollUtil.isEmpty(messageConsumer.avaliableTopics())) {
            throw new NoSuchElementException("none topic to listen");
        }
    }

    /**
     * 导入消息消费器
     *
     * @param messageConsumer
     * @return
     */
    public abstract AbstractMessageCenter setMessageConsumer(IMessageConsumer messageConsumer);

    /**
     * 移除消息消费器
     *
     * @param messageConsumer 目标消息消费器
     * @return true 移除成功 ，false移除失败
     */
    public abstract boolean removeMessageConsumer(IMessageConsumer messageConsumer);

    /**
     * 发布消息
     *
     * @param message
     */
    public void publish(IMessage message) {
        if (message == null) {
            throw new NullPointerException("message is null");
        }
        int tryTimes = 0;
        while (true) {
            try {
                sendMessage(message);
                break;
            } catch (Exception e) {
                onMessageFailed(message, tryTimes++, MessageHandleType.Send, e, null);
            }
        }
        onMessageSent(message);
    }

    protected abstract void sendMessage(IMessage message);

    /**
     * 当成功获取消息时
     *
     * @param message 接收到的消息
     * @return 是否成功被消费
     */
    protected abstract boolean onMessageReceived(IMessage message);

    /**
     * 当消息成功发送
     *
     * @param message
     */
    protected abstract void onMessageSent(IMessage message);

    /**
     * 当消息处理失败
     *
     * @param message  失败的消息
     * @param retryed  已重试的次数
     * @param type     处理方式
     * @param consumer
     * @return 是否重试
     */
    protected abstract boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type,
        Throwable throwable, IMessageConsumer consumer);

    /**
     * 根据消息选择消息消费者
     *
     * @param messageConsumer
     * @return
     */
    protected abstract List<IMessageConsumer> selectMessageConsumer(IMessage message);

    /**
     * 消息中心启动
     */
    public abstract void centerStartToWork();

    /**
     * 消息中心停止
     */
    public abstract void centerStopToWork();

    public abstract boolean isStart();

}
