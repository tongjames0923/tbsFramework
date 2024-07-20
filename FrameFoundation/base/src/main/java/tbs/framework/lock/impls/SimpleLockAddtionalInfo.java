package tbs.framework.lock.impls;

import org.jetbrains.annotations.NotNull;
import tbs.framework.lock.ILock;
import tbs.framework.proxy.IProxy;

/**
 * @author Abstergo
 */
public class SimpleLockAddtionalInfo implements IProxy.IProxyAdditionalInfo {

    ILock target = null;

    public SimpleLockAddtionalInfo(@NotNull ILock target) {
        this.target = target;
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
        if (clazz != ILock.class) {
            return null;
        }
        return (T)target;
    }
}
