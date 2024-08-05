package tbs.framework.auth.interfaces.debounce.impls;

import tbs.framework.auth.annotations.Debounce;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.debounce.IDebounce;
import tbs.framework.auth.model.RuntimeData;

import java.lang.reflect.Method;

/**
 * 防抖拦截
 *
 * @author abstergo
 */
public class DebounceInterceptor implements IApiInterceptor {

    IDebounce debounce;

    public DebounceInterceptor(IDebounce debounce) {
        this.debounce = debounce;
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
        return true;
    }
}
