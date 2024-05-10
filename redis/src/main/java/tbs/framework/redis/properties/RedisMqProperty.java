package tbs.framework.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tbs.framework.redis.mq")
public class RedisMqProperty {
    private int listenerPoolSize = 4;
}
