package tbs.framework.base.lock.expections;

/**
 * 锁获取失败异常
 * @author abstergo
 */
public class ObtainLockFailException extends Exception {
    public ObtainLockFailException(final String s) {
        super(s);
    }
}
