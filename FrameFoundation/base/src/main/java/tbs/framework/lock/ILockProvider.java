package tbs.framework.lock;

public interface ILockProvider {
    ILock getLocker(Object target);
}
