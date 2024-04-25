package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义权限提供者
 */
public interface IPermissionProvider {
    /**
     * 提供权限
     *
     * @param userModel 用户数据
     * @param url       路径
     * @param method    调用函数方法
     * @return 该url上的权限数据
     */
    List<PermissionModel> retrievePermissions(UserModel userModel, String url, Method method);
}
