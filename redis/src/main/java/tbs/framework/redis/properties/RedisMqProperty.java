package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis消息队列配置
 *
 * @author abstergo
 */
@Data
@ConfigurationProperties("tbs.framework.redis.mq")
public class RedisMqProperty {

    /**
     * 消费者线程池大小
     */
    private int listenerPoolSize = 4;
    /**
     * 消息序列化类
     */
    private Class<? extends RedisSerializer> messageSerializerClass = JdkSerializationRedisSerializer.class;
}
