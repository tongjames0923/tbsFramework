package tbs.framework.lock.expections;

/**
 * 锁获取失败异常
 * @author abstergo
 */
public class ObtainLockFailException extends RuntimeException {
    public ObtainLockFailException(final String s) {
        super(s);
    }
}
