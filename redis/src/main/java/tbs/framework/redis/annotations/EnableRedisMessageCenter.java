package tbs.framework.redis.annotations;

import org.springframework.context.annotation.Import;
import tbs.framework.redis.config.MsgConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@EnableTbsRedis
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MsgConfig.class)
public @interface EnableRedisMessageCenter {
}
