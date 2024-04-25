package tbs.framework.auth.interfaces.impls.permissionCheck;

import tbs.framework.auth.interfaces.ICustomPermissionChecker;
import tbs.framework.auth.interfaces.IPermissionProvider;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 不自定义检验权限和获取权限
 *
 * @author abstergo
 */
public class NotCustom implements ICustomPermissionChecker, IPermissionProvider {


    @Override
    public PermissionModel.VerificationResult checkPermission(PermissionModel permission, UserModel u) {
        throw new UnsupportedOperationException("Not Custom");
    }

    @Override
    public List<PermissionModel> retrievePermissions(UserModel userModel, String url, Method method) {
        throw new UnsupportedOperationException("Not Custom");
    }
}
