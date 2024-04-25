package tbs.framework.auth.interfaces.impls.permissionCheck;

import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import java.util.function.BiFunction;

public class NotCustom implements BiFunction<PermissionModel, UserModel, PermissionModel.VerificationResult> {
    @Override
    public PermissionModel.VerificationResult apply(PermissionModel permissionModel, UserModel userModel) {
        throw new IllegalStateException("Not Custom");
    }
}
