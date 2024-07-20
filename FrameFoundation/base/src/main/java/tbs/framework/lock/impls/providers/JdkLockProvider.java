package tbs.framework.lock.impls.providers;

import tbs.framework.lock.ILock;
import tbs.framework.lock.ILockProvider;
import tbs.framework.lock.impls.LockAdapter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Abstergo
 */
public class JdkLockProvider implements ILockProvider {
    @Override
    public ILock getLocker(Object target) {
        return new LockAdapter(new ReentrantLock());
    }
}
