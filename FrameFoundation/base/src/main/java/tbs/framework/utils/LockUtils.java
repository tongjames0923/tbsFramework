package tbs.framework.utils;

import tbs.framework.lock.ILock;
import tbs.framework.lock.ILockProvider;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

import javax.annotation.Resource;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abstergo
 */
public class LockUtils {

    public static LockUtils getInstance() {
        return SingletonHolder.getInstance(LockUtils.class);
    }

    private final ConcurrentHashMap<Object, WeakReference<ILock>> lockMap = new ConcurrentHashMap<>();

    @Resource
    private ILockProvider lockProvider;

    @AutoLogger
    private ILogger logger;


    public ILock getLock(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Lock target cannot be null.");
        }

        // Use ConcurrentHashMap to simplify the synchronization logic.
        return lockMap.computeIfAbsent(target, key -> {
            ILock newLock = lockProvider.getLocker(target);
            if (newLock == null) {
                throw new IllegalStateException("Failed to create a new lock instance.");
            }
            logger.info("Created new lock for target: {}, isReMake: {}", target, false);
            return new WeakReference<>(newLock);
        }).get();
    }
}
