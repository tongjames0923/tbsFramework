package tbs.framework.mq;

import tbs.framework.mq.message.IMessage;

/**
 * 消息数据源接口，用于添加和获取消息。
 */
public interface IMessageDataSource {
    /**
     * 添加消息到数据源。
     *
     * @param message 要添加的消息对象，实现 {@link IMessage} 接口。
     */
    void addMessage(IMessage message);

    /**
     * 从数据源中获取一条消息。
     *
     * @return 获取到的消息对象，实现 {@link IMessage} 接口。
     */
    IMessage getMessage();
}
