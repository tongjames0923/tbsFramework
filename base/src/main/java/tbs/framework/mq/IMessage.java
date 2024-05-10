package tbs.framework.mq;

import java.io.Serializable;
import java.util.Map;

public interface IMessage extends Serializable {
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
    
}
