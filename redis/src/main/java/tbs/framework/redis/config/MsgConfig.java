package tbs.framework.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.event.IMessageQueueEvents;
import tbs.framework.redis.impls.lock.RedisTaksBlockLock;
import tbs.framework.redis.impls.mq.RedisMessageCenter;
import tbs.framework.redis.impls.mq.receiver.RedisMessageConnector;
import tbs.framework.redis.impls.mq.sender.RedisSender;
import tbs.framework.redis.properties.RedisMqProperty;
import tbs.framework.redis.properties.RedisProperty;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

public class MsgConfig {

    @Resource
    RedisMqProperty redisMqProperty;



    @Bean("REDIS_MSG_SERI")
    RedisSerializer redisMqSerializer() throws Exception {
        if (redisMqProperty.getMessageSerializerClass() == null) {
            return new JdkSerializationRedisSerializer();
        } else {
            return redisMqProperty.getMessageSerializerClass().getConstructor().newInstance();
        }
    }

    @Bean("REDIS_MSG")
    RedisTemplate<String, Object> redisMsg(RedisConnectionFactory connectionFactory,
        @Qualifier("REDIS_MSG_SERI") RedisSerializer serializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        // key采用String的序列化方式
        template.setKeySerializer(serializer);

        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(serializer);

        // value序列化方式采用jackson
        template.setValueSerializer(serializer);

        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(serializer);
        template.setDefaultSerializer(serializer);
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
    RedisSender redisSender(@Qualifier("REDIS_MSG") RedisTemplate<String, Object> redisTemplate) {
        return new RedisSender(redisTemplate);
    }

    @Bean
    RedisMessageConnector redisMessageConnector() {
        return new RedisMessageConnector();
    }

    @Bean
    RedisMessageCenter abstractMessageCenter(RedisMessageListenerContainer redisMessageListenerContainer,
        RedisTaksBlockLock lock, RedisProperty redisProperty, RedisSender sender,
        IMessageConsumerManager consumerManager, IMessageQueueEvents events) {
        return new RedisMessageCenter(redisMessageListenerContainer, redisProperty, lock, sender, events,
            consumerManager);
    }

}
