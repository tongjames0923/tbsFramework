package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tbs.framework.redis.constants.RedisBeanNameConstants;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties("tbs.framework.redis")
public class RedisProperty {
    /**
     * redis键的序列化器
     */
    private Class<? extends RedisSerializer> KeySerializer = StringRedisSerializer.class;
    /**
     * redis值的序列化器
     */
    private Class<? extends RedisSerializer> ValueSerializer = Jackson2JsonRedisSerializer.class;
    /**
     * redis哈希键的序列化器
     */
    private Class<? extends RedisSerializer> HashKeySerializer = StringRedisSerializer.class;
    /**
     * redis哈希值的序列化器
     */
    private Class<? extends RedisSerializer> HashValueSerializer = Jackson2JsonRedisSerializer.class;
    /**
     * springboot默认redisCacheing功能的缓存时间
     */
    private long cacheTimeout = 3000;

    /**
     * springboot默认redisCacheing功能的是否接收空值
     */
    private boolean allowNullValues = false;

    /**
     * 缓存服务的前缀
     */
    private String cacheKeyPrefix = "REDIS-CACHE-:";

    /**
     *
     */
    private String cacheSource = RedisBeanNameConstants.DEFAULT_REDIS_TEMPLATE;

}
