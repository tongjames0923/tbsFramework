package tbs.framework.mq;

import java.util.Map;

public interface IMessage {
    /**
     * 消息类型（话题）
     *
     * @return
     */
    String getTopic();

    /**
     * 消息标签（备注）
     *
     * @return
     */

    String getTag();

    /**
     * 消息id
     *
     * @return
     */

    String getMessageId();

    /**
     * 消息优先级
     *
     * @return
     */
    long getPriority();

    /**
     * 消息参数
     *
     * @return
     */

    Map<String, Object> parmamterMap();

    /**
     * 设置消息已被消费
     */

    void setConsumed();

    /**
     * 消息是否被消费
     *
     * @return
     */
    boolean consumed();
}
