package tbs.framework.auth.config.interceptors;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.exceptions.TokenNotFoundException;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.log.ILogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token拦截器
 * @author abstergo
 */
public class TokenInterceptor implements HandlerInterceptor {

    IRequestTokenPicker tokenPicker;

    ILogger logger;

    public TokenInterceptor(final IRequestTokenPicker tokenPicker) {
        this.tokenPicker = tokenPicker;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {

        RuntimeData.getInstance().setRequestToken(this.tokenPicker.getToken(request, response));
        RuntimeData.getInstance().setInvokeUrl(request.getRequestURI());
        if (!StrUtil.isEmpty(RuntimeData.getInstance().getRequestToken())) {
            RuntimeData.getInstance().setStatus(RuntimeData.TOKEN_PASS);
        } else {
            throw new TokenNotFoundException("Token尚未传递");
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
