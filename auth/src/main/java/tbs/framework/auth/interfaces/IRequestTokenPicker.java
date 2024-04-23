package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.UserModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IRequestTokenPicker {
    String getToken(HttpServletRequest request, HttpServletResponse response);
}
