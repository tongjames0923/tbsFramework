package tbs.framework.redis.annotations;

import org.intellij.lang.annotations.Language;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import tbs.framework.base.annotations.EnableTbsFramework;
import tbs.framework.redis.config.BasicRedisConfig;
import tbs.framework.redis.properties.RedisAsyncTaskProperty;
import tbs.framework.redis.properties.RedisProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@EnableTbsFramework
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties({RedisProperty.class, RedisAsyncTaskProperty.class})
@Import(BasicRedisConfig.class)
public @interface EnableTbsRedis {
}
