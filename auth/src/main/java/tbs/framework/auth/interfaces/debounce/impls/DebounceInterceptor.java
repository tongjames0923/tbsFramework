package tbs.framework.auth.interfaces.debounce.impls;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import tbs.framework.auth.annotations.Debounce;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.debounce.IDebounce;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.properties.DebounceProperty;
import tbs.framework.auth.utils.PathUtil;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 防抖拦截
 *
 * @author abstergo
 */
public class DebounceInterceptor implements IApiInterceptor {

    @AutoLogger
    ILogger logger;

    IDebounce debounce;

    Set<String> path = new HashSet<>();

    ConcurrentMap<String, Boolean> urlSupportCache = new ConcurrentHashMap<>();

    public DebounceInterceptor(IDebounce debounce, DebounceProperty debounceProperty) {
        this.debounce = debounce;
        if (CollUtil.isEmpty(debounceProperty.getDebouncePathPattern())) {
            logger.warn("debouncePathPattern为空，将不会进行防抖");
            return;
        }
        debounceProperty.getDebouncePathPattern().forEach(p -> path.add(p));
    }

    @Override
    public void beforeInvoke(Method function, Object target, Object[] args) throws RuntimeException {
        Debounce debounce = function.getDeclaredAnnotation(Debounce.class);
        if (debounce == null) {
            return;
        }
        if (!RuntimeData.userLogined()) {
            throw new RuntimeException("用户未登录且接口为防抖接口，禁止访问");
        }

        this.debounce.debounce(RuntimeData.getInstance().getInvokeUrl(), RuntimeData.getInstance().getUserModel(),
            function, target, args);

    }

    @Override
    public void afterInvoke(Method function, Object target, Object[] args, Object result) throws RuntimeException {

    }

    @Override
    public boolean support(String url) {
        if (StrUtil.isEmpty(url)) {
            logger.warn("url should not be null or empty");
            return false;
        }
        return urlSupportCache.computeIfAbsent(url, k -> path.stream().anyMatch(p -> PathUtil.match(url, p)));
    }
}
