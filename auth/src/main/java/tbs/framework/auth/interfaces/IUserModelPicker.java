package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.UserModel;

/**
 * 用户数据获取 根据token
 *
 * @author abstergo
 */
public interface IUserModelPicker {
    /**
     * 获取用户数据
     * @param token 请求token
     * @return null则查不到用户数据
     */
    UserModel getUserModel(String token);
}
