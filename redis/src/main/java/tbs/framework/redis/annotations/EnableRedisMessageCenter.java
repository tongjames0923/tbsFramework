package tbs.framework.redis.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.annotations.EnableMessageQueue;
import tbs.framework.redis.config.MsgConfig;
import tbs.framework.redis.properties.RedisMqProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@EnableTbsRedis
@EnableMessageQueue
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MsgConfig.class)
@EnableConfigurationProperties({RedisMqProperty.class})
public @interface EnableRedisMessageCenter {
}
