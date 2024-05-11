package tbs.framework.redis.impls.mq.sender;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.mq.sender.IMessagePublisher;
import tbs.framework.redis.impls.mq.receiver.RedisMessageReceiver;

/**
 * @author abstergo
 */
public class RedisSender implements IMessagePublisher {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisSender(RedisTemplate<String, Object> redisTemplate) {
        if (!(redisTemplate.getValueSerializer() instanceof JdkSerializationRedisSerializer)) {
            throw new IllegalArgumentException("Redis serializer must be JdkSerializationRedisSerializer");
        }
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publishAll(IMessage... message) {
        for (IMessage m : message) {
            if (m != null && !StrUtil.isEmpty(m.getTopic())) {
                redisTemplate.convertAndSend(RedisMessageReceiver.TOPIC_PREFIX + m.getTopic(), m);
            }
        }
    }
}
