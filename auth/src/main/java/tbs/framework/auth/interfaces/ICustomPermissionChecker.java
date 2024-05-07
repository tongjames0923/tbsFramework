package tbs.framework.auth.interfaces;

import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

/**
 * 自定义权限检验接口
 * @author Abstergo
 */
public interface ICustomPermissionChecker {
    /**
     * 检验权限
     * @param permission 所需权限信息
     * @param u 用户数据实体
     * @return 检验结果
     */
    PermissionModel.VerificationResult checkPermission(PermissionModel permission, UserModel u);
}
