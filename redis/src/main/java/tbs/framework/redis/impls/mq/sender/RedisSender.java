package tbs.framework.redis.impls.mq.sender;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.RedisTemplate;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.mq.receiver.RedisMessageConnector;

/**
 * @author abstergo
 */
public class RedisSender implements IMessagePublisher {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisSender(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publishAll(IMessage... message) {
        for (IMessage m : message) {
            if (m != null && !StrUtil.isEmpty(m.getTopic())) {
                String topic = RedisMessageConnector.TOPIC_PREFIX + m.getTopic();
                redisTemplate.convertAndSend(topic, m);
            }
        }
    }
}
