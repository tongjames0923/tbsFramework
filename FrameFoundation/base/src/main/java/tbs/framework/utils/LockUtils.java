package tbs.framework.utils;

import tbs.framework.lock.ILock;
import tbs.framework.lock.ILockProvider;

import javax.annotation.Resource;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author abstergo
 */
public class LockUtils {

    private static LockUtils instance;

    public static LockUtils getInstance() {
        return instance;
    }

    public LockUtils() {
        instance = this;
    }

    private Map<Object, WeakReference<ILock>> iLockConcurrentHashMap = new HashMap<>();

    @Resource
    ILockProvider lockProvider;

    public ILock getLock(Object target) {
        if (target == null) {
            throw new UnsupportedOperationException("can not be null for lock target");
        }
        WeakReference<ILock> lock = null;
        synchronized (target) {
            lock = iLockConcurrentHashMap.get(target);
            if (lock == null || lock.get() == null) {
                LoggerUtils.getInstance().getLogger(ThreadUtil.class)
                    .info("create new lock for target:{},isReMake:{} ", target, lock != null);
                lock = new WeakReference<>(lockProvider.getLocker(target));
            }
            iLockConcurrentHashMap.put(target, lock);
        }
        return lock.get();
    }
}
