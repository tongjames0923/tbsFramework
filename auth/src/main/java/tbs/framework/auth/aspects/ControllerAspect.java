package tbs.framework.auth.aspects;

import cn.hutool.core.collection.CollUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tbs.framework.auth.annotations.ApplyRuntimeData;
import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.auth.interfaces.IPermissionValidator;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.log.ILogger;
import tbs.framework.utils.LogUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author abstergo
 */
@RestControllerAdvice
@Aspect
public class ControllerAspect implements ResponseBodyAdvice<Object> {

    private final ILogger logger;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {

    }

    public ControllerAspect(final LogUtil logUtil, final Map<String, IPermissionValidator> permissionValidators) {
        this.logger = logUtil.getLogger(ControllerAspect.class.getName());
        this.permissionValidators = permissionValidators;
    }

    @Resource
    IRuntimeDataExchanger exchanger;

    @Resource
    IErrorHandler errorHandler;

    @ExceptionHandler
    @ResponseBody
    public Object errorHandle(final Throwable e) {
        return this.errorHandler.handleError(e);
    }


    @Resource
    RuntimeData runtimeData;

    Map<String, IPermissionValidator> permissionValidators;



    @Around("requestMapping()")
    public Object controllerAspect(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

        this.runtimeData.setInvokeArgs(joinPoint.getArgs());
        this.runtimeData.setInvokeMethod(methodSignature.getMethod());
        if (RuntimeData.USER_PASS.equals(this.runtimeData.getStatus())) {
            this.logger.trace("permission check: " + methodSignature);
            this.checkPermissions();
        } else {
            this.logger.trace("permission check skiped: " + methodSignature);
        }

        this.logger.trace("executing method: " + methodSignature);
        this.runtimeData.setInvokeBegin(LocalDateTime.now());
            result = joinPoint.proceed();
        this.runtimeData.setInvokeEnd(LocalDateTime.now());

        return result;
    }

    private void checkPermissions() {
        for (final Map.Entry<String, IPermissionValidator> entry : this.permissionValidators.entrySet()) {
            final Set<PermissionModel> list =
                entry.getValue().pullPermission(this.runtimeData.getInvokeUrl(), this.runtimeData.getInvokeMethod());
            if (CollUtil.isEmpty(list)) {
                continue;
            }
            for (final PermissionModel permissionModel : list) {
                final PermissionModel.VerificationResult validate =
                    entry.getValue().validate(permissionModel, this.runtimeData.getUserModel());
                if (validate.hasError()) {
                    throw validate.getError();
                } else {
                    throw new RuntimeException(validate.getMessage());
                }
            }
        }
    }

    @Override
    public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType) {
        final ApplyRuntimeData applyRuntimeData = returnType.getMethodAnnotation(ApplyRuntimeData.class);
        return null != applyRuntimeData;
    }

    @Override
    public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType,
        final Class<? extends HttpMessageConverter<?>> selectedConverterType, final ServerHttpRequest request,
        final ServerHttpResponse response) {
        if (null == body) {
            return body;
        }
        return this.exchanger.exchange(this.runtimeData, body);
    }
}
