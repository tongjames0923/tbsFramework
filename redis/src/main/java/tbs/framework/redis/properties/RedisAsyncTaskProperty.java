package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties("tbs.framework.async.task.redis")
public class RedisAsyncTaskProperty {
    private Duration timeout = Duration.ofSeconds(30);

    private String keyPrefix = "redis-async-task-id:";
}
