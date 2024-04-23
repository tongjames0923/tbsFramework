package tbs.framework.auth.config.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.interfaces.IRequestTokenPicker;
import tbs.framework.auth.model.RuntimeData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author abstergo
 */
public class TokenInterceptor implements HandlerInterceptor {

    IRequestTokenPicker tokenPicker;


    public TokenInterceptor(IRequestTokenPicker tokenPicker) {
        this.tokenPicker = tokenPicker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        RuntimeData.getInstance().setRequestToken(tokenPicker.getToken(request, response));
        RuntimeData.getInstance().setInvokeUrl(request.getRequestURI());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
