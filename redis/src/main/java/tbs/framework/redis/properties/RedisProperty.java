package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Data
@ConfigurationProperties("tbs.framework.redis")
public class RedisProperty {
    private Class<? extends RedisSerializer> KeySerializer = StringRedisSerializer.class;
    private Class<? extends RedisSerializer> ValueSerializer = Jackson2JsonRedisSerializer.class;
    private Class<? extends RedisSerializer> HashKeySerializer = StringRedisSerializer.class;
    private Class<? extends RedisSerializer> HashValueSerializer = Jackson2JsonRedisSerializer.class;
    private long cacheTimeout = 3000;
    private boolean allowNullValues = false;
}
