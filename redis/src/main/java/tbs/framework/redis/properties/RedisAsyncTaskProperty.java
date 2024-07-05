package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * The type Redis async task property.
 * @author Abstergo
 */
@Data
@ConfigurationProperties("tbs.framework.async.task.redis")
public class RedisAsyncTaskProperty {


    /**
     * 任务数据的key前缀
     */
    private String keyPrefix = "redis-async-task-id:";
}
