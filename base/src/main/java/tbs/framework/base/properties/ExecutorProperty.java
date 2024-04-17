package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Data
@ConfigurationProperties(prefix = "tbs.framework.async")
public class ExecutorProperty {
    private int corePoolSize = 4;
    private int maxPoolSize = 32;
    private int keepAliveTime = 60;
    private int queueCapacity = 64;
    private Class<? extends RejectedExecutionHandler> rejectedExecutionHandler =
        ThreadPoolExecutor.CallerRunsPolicy.class;
}
