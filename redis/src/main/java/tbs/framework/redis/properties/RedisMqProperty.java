package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Data
@ConfigurationProperties("tbs.framework.redis.mq")
public class RedisMqProperty {


    private int listenerPoolSize = 4;
    /**
     * 消息中心仅允许消费一次
     */
    private boolean messageHandleOnce = true;

    private Class<? extends RedisSerializer> messageSerializerClass = JdkSerializationRedisSerializer.class;
}
