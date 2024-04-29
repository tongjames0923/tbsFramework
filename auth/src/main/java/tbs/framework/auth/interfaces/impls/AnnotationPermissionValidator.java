package tbs.framework.auth.interfaces.impls;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.auth.annotations.PermissionValidated;
import tbs.framework.auth.annotations.PermissionValidateds;
import tbs.framework.auth.interfaces.IPermissionProvider;
import tbs.framework.auth.interfaces.IPermissionValidator;
import tbs.framework.auth.interfaces.impls.permissionCheck.NotCustom;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.UserModel;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AnnotationPermissionValidator implements IPermissionValidator {

    @Override
    public Set<PermissionModel> pullPermission(final String url, final Method method) {
        final List<PermissionValidated> permissionValidateds = AnnotationPermissionValidator.getPermissionValidateds(method);
        final Set<PermissionModel> permissions = new HashSet<>(permissionValidateds.size());
        for (final PermissionValidated permissionValidated : permissionValidateds) {
            if (NotCustom.class != permissionValidated.userPermissionProvider()) {
                final IPermissionProvider permissionProvider =
                    SpringUtil.getBean(permissionValidated.userPermissionProvider());
                permissions.addAll(
                    permissionProvider.retrievePermissions(RuntimeData.getInstance().getUserModel(), url, method));
                continue;
            }
            final PermissionModel permission = new PermissionModel();
            permission.setUrl(url);
            permission.setRole(permissionValidated.value());
            if (NotCustom.class != permissionValidated.customCheck()) {
                permission.setParameter(permissionValidated.customCheck());
            }
            permissions.add(permission);
        }
        return permissions;
    }

    private static List<PermissionValidated> getPermissionValidateds(final Method method) {
        final Set<PermissionValidateds> permissionValidateds =
            AnnotatedElementUtils.getAllMergedAnnotations(method, PermissionValidateds.class);
        final List<PermissionValidated> validateds = new LinkedList<>();
        for (final PermissionValidateds permissionValidated : permissionValidateds) {
            for (final PermissionValidated validated : permissionValidated.value()) {
                if (null != validated) {
                    validateds.add(validated);
                }
            }
        }

        validateds.addAll(
            new ArrayList<>(AnnotatedElementUtils.getAllMergedAnnotations(method, PermissionValidated.class)));
        return validateds;
    }

    @Override
    public PermissionModel.VerificationResult validate(final PermissionModel permission, final UserModel userModel) {
        if (null != permission.getParameter()) {
            final Class<? extends BiFunction<PermissionModel, UserModel, PermissionModel.VerificationResult>> object =
                (Class<? extends BiFunction<PermissionModel, UserModel, PermissionModel.VerificationResult>>)permission.getParameter();
            return SpringUtil.getBean(object).apply(permission, userModel);
        }
        final Set<String> roles = Optional.ofNullable(userModel).map(UserModel::getUserRole).orElse(new HashSet<>());
        final boolean success = roles.contains(permission.getRole());
        if (success) {
            return PermissionModel.VerificationResult.success("success");
        } else {
            return PermissionModel.VerificationResult.reject(
                String.format("no role[%s] for user [%s] at url[%s]", permission.getRole(),
                    Optional.ofNullable(userModel).map(UserModel::getUserId).orElse("NULL ID"), permission.getUrl()));
        }
    }
}
