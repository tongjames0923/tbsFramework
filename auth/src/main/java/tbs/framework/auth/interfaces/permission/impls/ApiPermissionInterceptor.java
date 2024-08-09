package tbs.framework.auth.interfaces.permission.impls;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.permission.IPermissionValidator;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.properties.AuthProperty;
import tbs.framework.auth.utils.PathUtil;
import tbs.framework.log.ILogger;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 接口权限拦截器
 *
 * @author abstergo
 */
public class ApiPermissionInterceptor implements IApiInterceptor {

    @Resource
    ILogger logger;

    @Resource
    AuthProperty property;

    ConcurrentMap<String, Boolean> urlCache = new ConcurrentHashMap<>(32);

    @Override
    public void beforeInvoke(Method function, Object target, Object[] args) throws RuntimeException {
        if (RuntimeData.getInstance().getUserModel() != null) {
            this.logger.trace("permission check: " + function.toGenericString());
            this.checkPermissions(function);
        } else {
            this.logger.trace("permission check skiped: " + function.toGenericString());
        }

        this.logger.trace("executing method: " + function.toGenericString());
    }

    @Override
    public void afterInvoke(Method function, Object target, Object[] args, Object result) throws RuntimeException {
        this.logger.trace("executed method: {},return:[{}]", function.toGenericString(),
            result == null ? "null" : result.toString());
    }

    private void checkPermissions(Method method) {
        for (final Map.Entry<String, IPermissionValidator> entry : this.permissionValidators.entrySet()) {
            final Set<PermissionModel> list =
                entry.getValue().pullPermission(RuntimeData.getInstance().getInvokeUrl(), method);
            if (CollUtil.isEmpty(list)) {
                continue;
            }
            for (final PermissionModel permissionModel : list) {
                final PermissionModel.VerificationResult validate =
                    entry.getValue().validate(permissionModel, RuntimeData.getInstance().getUserModel());
                if (validate.hasError()) {
                    throw validate.getError();
                } else {
                    this.logger.trace("permission check success: " + permissionModel);
                }
            }
        }
    }

    Map<String, IPermissionValidator> permissionValidators;

    public ApiPermissionInterceptor(Map<String, IPermissionValidator> permissionValidators) {
        this.permissionValidators = permissionValidators;
    }

    @Override
    public boolean support(String url) {
        if (CollUtil.isEmpty(this.property.getAuthPathPattern()) || StrUtil.isEmpty(url)) {
            logger.warn("auth path pattern is empty or url is empty");
            return false;
        }
        return urlCache.computeIfAbsent(url, k -> {
            for (String p : property.getAuthPathPattern()) {
                if (PathUtil.match(url, p)) {
                    return true;
                }
            }
            return false;
        });
    }
}