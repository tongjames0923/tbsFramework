package tbs.framework.auth.interfaces.debounce.impls;

import com.alibaba.fastjson2.JSON;
import tbs.framework.auth.exceptions.DebounceException;
import tbs.framework.auth.interfaces.debounce.IDebounce;
import tbs.framework.auth.model.UserModel;
import tbs.framework.cache.managers.AbstractExpireManager;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * @author abstergo
 */
public class CacheDebounce implements IDebounce {

    private AbstractExpireManager expireManager;

    /**
     * 抖动冷却时间 单位毫秒
     */
    private int debounceTime = 200;

    public CacheDebounce(AbstractExpireManager expireManager, int debounceTime) {
        this.expireManager = expireManager;
        this.debounceTime = debounceTime;
    }

    private String debounceKey(String url, UserModel user, Method method, Object[] args) {
        return String.format("%s:%s:%s:%s", url, user.getUserId(), method.getName(), JSON.toJSONString(args));
    }

    @Override
    public void debounce(String url, UserModel user, Method method, Object target, Object[] args)
        throws DebounceException {
        Object value = expireManager.get(debounceKey(url, user, method, args));
        if (value == null) {
            expireManager.putAndRemove(debounceKey(url, user, method, args), 1, true, Duration.ofMillis(debounceTime));
        } else {
            throw new DebounceException("相同请求过于频繁");
        }
    }
}
