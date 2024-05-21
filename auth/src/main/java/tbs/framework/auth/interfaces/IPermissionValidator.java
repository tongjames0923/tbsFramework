package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;
import java.util.Set;

public interface IPermissionValidator {

    /**
     * 获取权限信息
     *
     * @param url
     * @param method
     * @return
     */
    Set<PermissionModel> pullPermission(String url, Method method);

    /**
     * 验证权限
     *
     * @param permission 权限
     * @param userModel  用户数据
     * @return 验证结果
     */
    PermissionModel.VerificationResult validate(PermissionModel permission, UserModel userModel);
}
