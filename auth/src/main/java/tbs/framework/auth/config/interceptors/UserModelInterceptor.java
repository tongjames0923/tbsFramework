package tbs.framework.auth.config.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import tbs.framework.auth.exceptions.UserModelNotFoundException;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *用户数据拦截器
 * @author abstergo
 */
public class UserModelInterceptor implements HandlerInterceptor {

    IUserModelPicker userModelPicker;
    ILogger logger;

    public UserModelInterceptor(IUserModelPicker userModelPicker, LogUtil logUtil) {
        this.userModelPicker = userModelPicker;
        logger = logUtil.getLogger(UserModelInterceptor.class.getName());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        RuntimeData.getInstance()
            .setUserModel(userModelPicker.getUserModel(RuntimeData.getInstance().getRequestToken()));

        if (RuntimeData.getInstance().getUserModel() != null) {
            RuntimeData.getInstance().setStatus(RuntimeData.USER_PASS);
        } else {
            throw new UserModelNotFoundException("用户数据不存在");
        }
        return true;
    }
}
