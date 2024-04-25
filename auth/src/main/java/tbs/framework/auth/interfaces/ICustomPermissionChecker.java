package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

public interface ICustomPermissionChecker {
    PermissionModel.VerificationResult checkPermission(PermissionModel permission, UserModel u);
}
