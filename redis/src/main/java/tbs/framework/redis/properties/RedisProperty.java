package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties("tbs.framework.redis")
public class RedisProperty {
    /**
     * 键序列化器
     */
    private Class<? extends RedisSerializer> KeySerializer = StringRedisSerializer.class;
    /**
     * 值序列化器
     */
    private Class<? extends RedisSerializer> ValueSerializer = Jackson2JsonRedisSerializer.class;
    /**
     * 哈希格式键序列化器
     */
    private Class<? extends RedisSerializer> HashKeySerializer = StringRedisSerializer.class;
    /**
     * 哈希格式值序列化器
     */
    private Class<? extends RedisSerializer> HashValueSerializer = Jackson2JsonRedisSerializer.class;
    /**
     * 缓存默认超时（springboot 默认框架的）
     */
    private long cacheTimeout = 3000;

    /**
     * springboot框架默认的是否允许空值
     */
    private boolean allowNullValues = false;
}
