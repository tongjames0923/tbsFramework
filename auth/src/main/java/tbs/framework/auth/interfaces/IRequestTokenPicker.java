package tbs.framework.auth.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求token获取
 *
 * @author abstergo
 */
public interface IRequestTokenPicker {
    /**
     * 获取token
     * @param request 请求
     * @param response 响应
     * @return token
     */
    String getToken(HttpServletRequest request, HttpServletResponse response);
}
