package tbs.framework.auth.aspects;

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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tbs.framework.auth.annotations.ApplyRuntimeData;
import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestControllerAdvice
@Aspect
public class ControllerAspect implements ResponseBodyAdvice<Object> {

    private ILogger logger;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {

    }

    public ControllerAspect(LogUtil logUtil) {
        logger = logUtil.getLogger(ControllerAspect.class.getName());
    }

    @Resource
    IRuntimeDataExchanger exchanger;

    @Resource
    IErrorHandler errorHandler;

    @Resource
    RuntimeData runtimeData;

    @Around("requestMapping()")
    public Object controllerAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        runtimeData.setInvokeArgs(joinPoint.getArgs());
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        runtimeData.setInvokeMethod(methodSignature.getMethod());
        logger.trace("executing method: " + methodSignature.toString());
        runtimeData.setInvokeBegin(LocalDateTime.now());
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            result = errorHandler.handleError(e, methodSignature.getReturnType(), result);
        }
        runtimeData.setInvokeEnd(LocalDateTime.now());
        return result;
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
