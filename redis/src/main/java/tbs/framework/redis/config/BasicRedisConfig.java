package tbs.framework.redis.config;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tbs.framework.cache.managers.AbstractExpiredHybridCacheManager;
import tbs.framework.lock.IReadWriteLock;
import tbs.framework.lock.impls.ReadWriteLockAdapter;
import tbs.framework.redis.cache.impls.RedisExpiredImpl;
import tbs.framework.redis.cache.impls.services.RedisCacheServiceImpl;
import tbs.framework.redis.constants.RedisBeanNameConstants;
import tbs.framework.redis.properties.RedisProperty;

import java.time.Duration;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author abstergo
 */
@ComponentScan(basePackages = "tbs.framework.redis")
public class BasicRedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private Integer port;
    @Value("${spring.redis.timeout:30000}")
    private long timeout;
    @Value("${spring.redis.password:123456}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + this.host + ":" + this.port).setPassword(this.password);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        return om;
    }

    @Bean
    Jackson2JsonRedisSerializer redisSerializer(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }

    @Bean
    StringRedisSerializer stringRedisSerializer() {

        return new StringRedisSerializer();
    }

    @Bean(RedisBeanNameConstants.DEFAULT_REDIS_TEMPLATE)
    @Primary
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, RedisProperty redisProperty) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        // key采用String的序列化方式
        template.setKeySerializer(SpringUtil.getBean(redisProperty.getKeySerializer()));

        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(SpringUtil.getBean(redisProperty.getHashKeySerializer()));

        // value序列化方式采用jackson
        template.setValueSerializer(SpringUtil.getBean(redisProperty.getValueSerializer()));

        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(SpringUtil.getBean(redisProperty.getHashValueSerializer()));

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheConfiguration.class)
    RedisCacheConfiguration redisConfiguration(RedisProperty property) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();

        property.setAllowNullValues(property.isAllowNullValues());

        redisCacheConfiguration.entryTtl(Duration.ofMillis(property.getCacheTimeout()));
        return redisCacheConfiguration;
    }

    @Bean
    @ConditionalOnMissingBean(RedisCacheManager.class)
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
        RedisCacheConfiguration redisCacheConfiguration, RedisProperty property) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
        return redisCacheManager;
    }



}
