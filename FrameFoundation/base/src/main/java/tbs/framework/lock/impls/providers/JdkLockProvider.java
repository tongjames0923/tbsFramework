package tbs.framework.lock.impls.providers;

import tbs.framework.lock.ILock;
import tbs.framework.lock.ILockProvider;
import tbs.framework.lock.impls.ReentrantLockImpl;

/**
 * @author Abstergo
 */
public class JdkLockProvider implements ILockProvider {
    @Override
    public ILock getLocker(Object target) {
        return new ReentrantLockImpl();
    }
}
