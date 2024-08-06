package tbs.framework.auth.interfaces.permission.impls;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.AntPathMatcher;
import com.alibaba.fastjson2.JSON;
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.permission.IPermissionValidator;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.properties.AuthProperty;
import tbs.framework.log.ILogger;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 接口权限拦截器
 *
 * @author abstergo
 */
public class ApiPermissionInterceptor implements IApiInterceptor {

    @Resource
    ILogger logger;

    @Resource
    RuntimeData runtimeData;

    @Resource
    AuthProperty property;

    Map<String, Boolean> urlCache = new HashMap<>(32);

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
            result == null ? "null" : JSON.toJSONString(result));
    }

    private void checkPermissions(Method method) {
        for (final Map.Entry<String, IPermissionValidator> entry : this.permissionValidators.entrySet()) {
            final Set<PermissionModel> list = entry.getValue().pullPermission(this.runtimeData.getInvokeUrl(), method);
            if (CollUtil.isEmpty(list)) {
                continue;
            }
            for (final PermissionModel permissionModel : list) {
                final PermissionModel.VerificationResult validate =
                    entry.getValue().validate(permissionModel, this.runtimeData.getUserModel());
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
        if (urlCache.containsKey(url)) {
            return true;
        }
        for (String p : property.getAuthPathPattern()) {
            AntPathMatcher pattern = new AntPathMatcher();
            if (pattern.match(p, url)) {
                urlCache.put(url, true);
                return true;
            }
        }
        return false;
    }
}