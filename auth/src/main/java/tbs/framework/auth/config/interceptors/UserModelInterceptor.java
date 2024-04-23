package tbs.framework.auth.config.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.model.RuntimeData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserModelInterceptor implements HandlerInterceptor {

    IUserModelPicker userModelPicker;

    public UserModelInterceptor(IUserModelPicker userModelPicker) {
        this.userModelPicker = userModelPicker;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        RuntimeData.getInstance()
            .setUserModel(userModelPicker.getUserModel(RuntimeData.getInstance().getRequestToken()));
        return true;
    }
}
