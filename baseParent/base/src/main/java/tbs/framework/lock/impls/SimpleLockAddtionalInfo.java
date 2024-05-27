package tbs.framework.lock.impls;

import tbs.framework.proxy.IProxy;

/**
 * @author Abstergo
 */
public class SimpleLockAddtionalInfo implements IProxy.IProxyAdditionalInfo {

    private String lockName;

    public SimpleLockAddtionalInfo(String lockName) {
        this.lockName = lockName;
    }

    public SimpleLockAddtionalInfo() {

    }

    /**
     * 仅支持String类型获取一个值作为LockId
     *
     * @param clazz String.class
     * @param key   无作用
     * @param <T>
     * @return
     */
    @Override
    public <T> T getInfoAs(Class<T> clazz, String key) {
        if (clazz != String.class) {
            return null;
        }
        return (T)lockName;
    }
}
