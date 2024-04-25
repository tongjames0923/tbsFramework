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
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Aspect
public class ControllerAspect implements ResponseBodyAdvice<Object> {

    private ILogger logger;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {

    }

    public ControllerAspect(LogUtil logUtil, Map<String, IPermissionValidator> permissionValidators) {
        logger = logUtil.getLogger(ControllerAspect.class.getName());
        this.permissionValidators = permissionValidators;
    }

    @Resource
    IRuntimeDataExchanger exchanger;

    @Resource
    IErrorHandler errorHandler;

    @ExceptionHandler
    @ResponseBody
    public Object errorHandle(Throwable e) {
        return errorHandler.handleError(e);
    }


    @Resource
    RuntimeData runtimeData;

    Map<String, IPermissionValidator> permissionValidators;



    @Around("requestMapping()")
    public Object controllerAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

        runtimeData.setInvokeArgs(joinPoint.getArgs());
        runtimeData.setInvokeMethod(methodSignature.getMethod());
        logger.trace("permission check: " + methodSignature.toString());
        if (RuntimeData.USER_PASS.equals(runtimeData.getStatus())) {
            checkPermissions();
        }

        logger.trace("executing method: " + methodSignature.toString());
        runtimeData.setInvokeBegin(LocalDateTime.now());
            result = joinPoint.proceed();
        runtimeData.setInvokeEnd(LocalDateTime.now());

        return result;
    }

    private void checkPermissions() throws IllegalAccessException {
        for (Map.Entry<String, IPermissionValidator> entry : permissionValidators.entrySet()) {
            List<PermissionModel> list =
                entry.getValue().pullPermission(runtimeData.getInvokeUrl(), runtimeData.getInvokeMethod());
            if (CollUtil.isEmpty(list)) {
                continue;
            }
            for (PermissionModel permissionModel : list) {
                PermissionModel.VerificationResult validate =
                    entry.getValue().validate(permissionModel, runtimeData.getUserModel());
                if (validate.success()) {
                    continue;
                } else if (validate.hasError()) {
                    throw validate.getError();
                } else {
                    throw new IllegalAccessException(validate.getMessage());
                }
            }
        }
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        ApplyRuntimeData applyRuntimeData = returnType.getMethodAnnotation(ApplyRuntimeData.class);
        return applyRuntimeData != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {
        if (body == null) {
            return body;
        }
        return exchanger.exchange(runtimeData, body);
    }
}
