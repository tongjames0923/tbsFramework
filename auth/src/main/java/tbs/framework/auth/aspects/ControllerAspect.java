package tbs.framework.auth.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
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
import tbs.framework.auth.interfaces.IApiInterceptor;
import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author abstergo
 */
@RestControllerAdvice
@Aspect
public class ControllerAspect implements ResponseBodyAdvice<Object> {

    @AutoLogger
    private ILogger logger;

    @Pointcut(
        "@annotation(org.springframework.web.bind.annotation.RequestMapping)||@annotation(org.springframework.web.bind.annotation.GetMapping)||@annotation(org.springframework.web.bind.annotation.PostMapping)||" +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)||@annotation(org.springframework.web.bind.annotation.DeleteMapping)||@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMapping() {

    }

    public ControllerAspect(Map<String, IApiInterceptor> interceptorMap) {
        this.interceptorMap = interceptorMap;
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

    Map<String, IApiInterceptor> interceptorMap;


    @Around("requestMapping()")
    public Object controllerAspect(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        this.runtimeData.setInvokeArgs(joinPoint.getArgs());
        final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        this.runtimeData.setInvokeMethod(methodSignature.getMethod());
        List<IApiInterceptor> accept = ApiInterceptorBeforeWork(joinPoint, methodSignature);
        this.runtimeData.setInvokeBegin(LocalDateTime.now());
        result = joinPoint.proceed();
        this.runtimeData.setInvokeEnd(LocalDateTime.now());
        for (IApiInterceptor interceptor : accept) {
            interceptor.afterInvoke(methodSignature.getMethod(), joinPoint.getTarget(), joinPoint.getArgs(), result);
        }
        return result;
    }

    @NotNull
    private List<IApiInterceptor> ApiInterceptorBeforeWork(ProceedingJoinPoint joinPoint,
        MethodSignature methodSignature) {
        List<IApiInterceptor> accept = new ArrayList<>(interceptorMap.size());
        for (IApiInterceptor interceptor : interceptorMap.values()) {
            if (interceptor.support(RuntimeData.getInstance().getInvokeUrl())) {
                accept.add(interceptor);
                interceptor.beforeInvoke(methodSignature.getMethod(), joinPoint.getTarget(), joinPoint.getArgs());
                logger.debug("{} before invoked for url {}", interceptor.getClass().getName(),
                    RuntimeData.getInstance().getInvokeUrl());
            }
        }
        return accept;
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
