package tbs.framework.mq.message.impls;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;
import tbs.framework.base.utils.UuidUtil;
import tbs.framework.mq.message.IMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author abstergo
 */
@NoArgsConstructor
public class SimpleMessage implements IMessage {

    private static final long serialVersionUID = -1918194218518995261L;
    private String topic;
    private String tag;
    private Map<String, Object> parmamterMap;
    private String messageId;
    private int priority = 0;

    private static final String CONSUMED = "CONSUMED_KEY";

    public SimpleMessage(String topic, String tag, Map<String, Object> headers, int priority) {
        this.topic = StrUtil.isEmpty(topic) ? "core" : topic;
        this.tag = tag;
        this.parmamterMap = headers == null ? new HashMap<>() : headers;
        this.messageId = UuidUtil.getUuid();
        this.priority = priority;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public long getPriority() {
        return priority;
    }

    @Override
    public Map<String, Object> getParmamterMap() {
        return parmamterMap;
    }

    @Override
    public String toString() {
        return "SimpleMessage{" +
            "topic='" +
            topic +
            '\'' +
            ", tag='" +
            tag +
            '\'' + ", parmamterMap=" + parmamterMap + ", messageId='" + messageId +
            '\'' +
            ", priority=" +
            priority +
            '}';
    }
}
