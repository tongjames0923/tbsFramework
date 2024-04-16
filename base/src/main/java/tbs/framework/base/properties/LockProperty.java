package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.base.lock.ILock;
import tbs.framework.base.lock.impls.JDKLock;

import java.util.concurrent.locks.Lock;

@Data
@ConfigurationProperties(prefix = "tbs.framework.base.lock")
public class LockProperty {
    /**
     * jdk类型锁的实现类
     */
    private Class<? extends Lock> jdkLock = java.util.concurrent.locks.ReentrantLock.class;

    /**
     * 锁类型
     */
    private Class<? extends ILock> lockType = JDKLock.class;
}
