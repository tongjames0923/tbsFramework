package tbs.framework.mq.receiver;

import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 消息接收器接口，定义了提供消息和来自消息连接器的方法。
 *
 * @author abstergo
 */
public interface IMessageReceiver {

    /**
     * 提供消息
     *
     * @return 接收到的消息
     */
    IMessage receive();

    /**
     * 来自的消息连接器
     *
     * @return IMessageConnector实例
     */
    IMessageConnector builder();

    /**
     * 消息接收器的接受Topic
     *
     * @return 接受Topic的集合
     */
    default Set<String> acceptTopics() {
        return new HashSet<>(Arrays.asList(".*"));
    }

}
