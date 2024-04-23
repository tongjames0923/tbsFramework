package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.impls.JdkLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.base.lock")
public class LockProperty {
    /**
     * jdk类型锁的实现类
     */
    private Class<? extends Lock> lockImpl = java.util.concurrent.locks.ReentrantLock.class;

    /**
     * 锁类型
     */
    private Class<? extends ILock> lockType = JdkLock.class;

    /**
     * 锁等待时间
     */
    private long lockTimeout = 10000;

    /**
     * 等待时间单位
     */
    private TimeUnit lockTimeUnit = TimeUnit.MILLISECONDS;
}
