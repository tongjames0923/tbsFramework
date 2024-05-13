package tbs.framework.mq.connector;

import tbs.framework.mq.receiver.IMessageReceiver;

import java.util.List;

/**
 * @author Abstergo
 */
public interface IMessageConnector {

    /**
     * 配置接收器使用当前的连接器
     * @param receivers
     */
    void factoryMessageReceivers(List<IMessageReceiver> receivers);

    /**
     * 使接收器失效，不再使用当前的连接器获取数据
     * @param receivers
     */
    void invalidateReceivers(List<IMessageReceiver> receivers);
}
