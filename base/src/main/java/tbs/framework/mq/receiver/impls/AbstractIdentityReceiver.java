package tbs.framework.mq.receiver.impls;

import cn.hutool.core.util.StrUtil;
import tbs.framework.utils.UuidUtil;
import tbs.framework.mq.receiver.IMessageReceiver;

/**
 * @author abstergo
 */
public abstract class AbstractIdentityReceiver implements IMessageReceiver {
    private String id = null;
    private boolean isOk = true;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractIdentityReceiver)) {
            return false;
        }

        AbstractIdentityReceiver that = (AbstractIdentityReceiver)o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * 接收器id
     *
     * @return
     */
    public String receiverId() {
        if (StrUtil.isEmpty(id)) {
            this.id = UuidUtil.getUuid();
        }
        return id;
    }

    public AbstractIdentityReceiver setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 接收器是否可用
     *
     * @return
     */
    public boolean avaliable() {
        return isOk;
    }

    public AbstractIdentityReceiver setAvaliable(boolean isAvaliable) {
        isOk = isAvaliable;
        return this;
    }
}
