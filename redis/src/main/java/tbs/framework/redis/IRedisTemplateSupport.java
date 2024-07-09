package tbs.framework.redis;

import org.springframework.data.redis.core.RedisTemplate;

public interface IRedisTemplateSupport {
    public RedisTemplate getRedisTemplate();
}
