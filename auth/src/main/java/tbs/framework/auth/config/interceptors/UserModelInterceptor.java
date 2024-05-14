package tbs.framework.auth.config.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.exceptions.UserModelNotFoundException;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.log.ILogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *用户数据拦截器
 * @author abstergo
 */
public class UserModelInterceptor implements HandlerInterceptor {

    IUserModelPicker userModelPicker;
    ILogger logger;

    public UserModelInterceptor(final IUserModelPicker userModelPicker) {
        this.userModelPicker = userModelPicker;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
        throws Exception {
        RuntimeData.getInstance()
            .setUserModel(this.userModelPicker.getUserModel(RuntimeData.getInstance().getRequestToken()));

        if (null != RuntimeData.getInstance().getUserModel()) {
            RuntimeData.getInstance().setStatus(RuntimeData.USER_PASS);
        } else {
            throw new UserModelNotFoundException("用户数据不存在");
        }
        return true;
    }
}
