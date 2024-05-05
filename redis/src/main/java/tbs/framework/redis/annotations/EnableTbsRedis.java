package tbs.framework.redis.annotations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.annotations.EnableTbsFramework;
import tbs.framework.redis.config.BasicRedisConfig;
import tbs.framework.redis.properties.RedisProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@EnableTbsFramework
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties(RedisProperty.class)
@Import(BasicRedisConfig.class)
public @interface EnableTbsRedis {
}
