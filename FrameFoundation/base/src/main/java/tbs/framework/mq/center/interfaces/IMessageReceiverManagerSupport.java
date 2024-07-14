package tbs.framework.mq.center.interfaces;

import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.List;

/**
 * @author abstergo
 */
public interface IMessageReceiverManagerSupport {
    /**
     * 获取消息接收器列表。
     *
     * @return 消息接收器列表。
     */
    public List<IMessageReceiver> getReceivers();

    /**
     * 添加消息接收器。
     *
     * @param receiver 消息接收器。
     */
    public void addReceivers(IMessageReceiver... receiver);

    /**
     * 移除消息接收器。
     *
     * @param receiver 消息接收器。
     */
    public void removeReceivers(IMessageReceiver... receiver);
}
