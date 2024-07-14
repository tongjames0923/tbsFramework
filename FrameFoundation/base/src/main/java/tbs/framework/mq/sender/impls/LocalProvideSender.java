package tbs.framework.mq.sender.impls;

import org.jetbrains.annotations.NotNull;
import tbs.framework.mq.IMessageDataSource;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;

/**
 * 本地消息发布器
 *
 * @author Abstergo
 */
public class LocalProvideSender implements IMessagePublisher {

    private IMessageDataSource messageQueue = null;

    public LocalProvideSender(IMessageDataSource messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void publish(IMessage m, @NotNull AbstractMessageCenter center) {
        // 将消息添加到消息队列
        messageQueue.addMessage(m);
    }
}
