package tbs.framework.mq.receiver;

import tbs.framework.mq.connector.IMessageConnector;
import tbs.framework.mq.message.IMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author abstergo
 */
public interface IMessageReceiver {

    /**
     * 提供消息
     *
     * @return 接收到的消息¬
     */
    IMessage receive();

    /**
     * 来自的消息连接器
     *
     * @return
     */
    IMessageConnector builder();

    /**
     * 消息接收器的接受Topic
     *
     * @return
     */
    default Set<String> acceptTopics() {
        return new HashSet<>(Arrays.asList(".*"));
    }

}
