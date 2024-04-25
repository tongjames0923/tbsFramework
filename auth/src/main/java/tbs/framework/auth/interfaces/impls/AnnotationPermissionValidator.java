package tbs.framework.auth.interfaces.impls;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.auth.annotations.PermissionValidated;
import tbs.framework.auth.interfaces.IPermissionValidator;
import tbs.framework.auth.interfaces.impls.permissionCheck.NotCustom;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AnnotationPermissionValidator implements IPermissionValidator {

    @Override
    public List<PermissionModel> pullPermission(String url, Method method) {
        List<PermissionValidated> permissionValidateds =
            new ArrayList<>(AnnotatedElementUtils.getAllMergedAnnotations(method, PermissionValidated.class));
        List<PermissionModel> permissions = new ArrayList<>(permissionValidateds.size());
        for (PermissionValidated permissionValidated : permissionValidateds) {
            PermissionModel permission = new PermissionModel();
            permission.setUrl(url);
            permission.setRole(permissionValidated.value());
            if (permissionValidated.customCheck() != NotCustom.class) {
                permission.setParameter(permissionValidated.customCheck());
            }
            permissions.add(permission);
        }
        return permissions;
    }

    @Override
    public PermissionModel.VerificationResult validate(PermissionModel permission, UserModel userModel) {
        if (permission.getParameter() != null) {
            Class<? extends BiFunction<PermissionModel, UserModel, PermissionModel.VerificationResult>> object =
                (Class<? extends BiFunction<PermissionModel, UserModel, PermissionModel.VerificationResult>>)permission.getParameter();
            return SpringUtil.getBean(object).apply(permission, userModel);
        }
        Set<String> roles = Optional.ofNullable(userModel).map(UserModel::getUserRole).orElse(new HashSet<>());
        boolean success = roles.contains(permission.getRole());
        if (success) {
            return PermissionModel.VerificationResult.success("success");
        } else {
            return PermissionModel.VerificationResult.reject(
                String.format("no role[%s] for user [%s] at url[%s]", permission.getRole(),
                    Optional.ofNullable(userModel).map(UserModel::getUserId).orElse("NULL ID"), permission.getUrl()));
        }
    }
}
