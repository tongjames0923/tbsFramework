package tbs.framework.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessageConsumerManager;
import tbs.framework.mq.IMessageQueueEvents;
import tbs.framework.redis.impls.RedisMessageCenter;
import tbs.framework.redis.impls.RedisTaksBlockLock;
import tbs.framework.redis.properties.RedisMqProperty;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

public class MsgConfig {

    @Resource
    RedisMqProperty redisMqProperty;

    @Bean("REDIS_MSG")
    RedisTemplate<String, Object> redisMsg(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        // key采用String的序列化方式
        template.setKeySerializer(new JdkSerializationRedisSerializer());

        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(new JdkSerializationRedisSerializer());

        // value序列化方式采用jackson
        template.setValueSerializer(new JdkSerializationRedisSerializer());

        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.setDefaultSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(Executors.newFixedThreadPool(redisMqProperty.getListenerPoolSize()));
        return container;
    }

    @Bean
    AbstractMessageCenter abstractMessageCenter(RedisMessageListenerContainer container,
        IMessageQueueEvents queueEvents, RedisProperty property, IMessageConsumerManager consumerManager,
        RedisTaksBlockLock blockLock) {
        return new RedisMessageCenter(container, consumerManager, queueEvents, property, blockLock);
    }
}
