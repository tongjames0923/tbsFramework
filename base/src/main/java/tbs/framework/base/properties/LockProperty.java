package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.impls.JdkLock;

import java.util.concurrent.TimeUnit;

/**
 * @author abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.base.lock")
public class LockProperty {

    /**
     * 锁类型
     */
    private Class<? extends ILock> proxyLockType = JdkLock.class;

    /**
     * 锁等待时间
     */
    private long proxyLockTimeout = 10000;

    /**
     * 等待时间单位
     */
    private TimeUnit proxyLockTimeUnit = TimeUnit.MILLISECONDS;
}
