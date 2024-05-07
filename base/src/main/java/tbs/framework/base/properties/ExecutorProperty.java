package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.async")
public class ExecutorProperty {
    /**
     * 核心线程池数
     */
    private int corePoolSize = 4;
    /**
     * 最大线程池数
     */
    private int maxPoolSize = 32;
    /**
     * 线程保活时间
     */
    private int keepAliveTime = 60;
    /**
     * 队列容量
     */
    private int queueCapacity = 64;
    /**
     * 拒绝策略
     */
    private Class<? extends RejectedExecutionHandler> rejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy.class;
}
