package tbs.framework.auth.interfaces.permission.impls.check;

import tbs.framework.auth.interfaces.permission.ICustomPermissionChecker;
import tbs.framework.auth.interfaces.permission.IPermissionProvider;
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
    public PermissionModel.VerificationResult checkPermission(final PermissionModel permission, final UserModel u) {
        throw new UnsupportedOperationException("Not Custom");
    }

    @Override
    public List<PermissionModel> retrievePermissions(final UserModel userModel, final String url, final Method method) {
        throw new UnsupportedOperationException("Not Custom");
    }
}
