package tbs.framework.auth.config.interceptors;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.model.RuntimeData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author abstergo
 */
public class RuntimeDataInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        if (StrUtil.isEmpty(RuntimeData.getInstance().getInvokeUrl())) {
            RuntimeData.getInstance().setInvokeUrl(request.getRequestURI());
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
