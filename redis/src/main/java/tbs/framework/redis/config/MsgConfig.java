package tbs.framework.redis.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import tbs.framework.mq.AbstractMessageCenter;
import tbs.framework.mq.IMessageConsumerManager;
import tbs.framework.mq.impls.consumer.manager.MappedConsumerManager;
import tbs.framework.redis.properties.RedisProperty;

import java.util.List;

public class MsgConfig {
    @Bean
    ApplicationRunner initMessageCenter(List<AbstractMessageCenter> abstractMessageCenters) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                for (AbstractMessageCenter abstractMessageCenter : abstractMessageCenters) {
                    abstractMessageCenter.afterPropertiesSet();
                }
            }
        };
    }

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
    IMessageConsumerManager consumerManager(RedisProperty redisProperty) throws Exception {
        if (redisProperty.getConsumerManager() == null) {
            return new MappedConsumerManager();
        }
        return redisProperty.getConsumerManager().getConstructor().newInstance();
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
