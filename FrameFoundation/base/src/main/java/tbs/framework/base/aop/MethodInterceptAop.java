package tbs.framework.base.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.base.annotations.MethodIntercept;
import tbs.framework.base.interfaces.IInterceptedTarget;
import tbs.framework.base.interfaces.IMethodInterceptHandler;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.utils.BeanUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
@Aspect
public class MethodInterceptAop {

    @Pointcut("@annotation(tbs.framework.base.annotations.MethodIntercept)")
    public void annotationed() {
    }

    @Pointcut("this(tbs.framework.base.interfaces.IInterceptedTarget)")
    public void interfaced() {

    }

    private static final int ByAnnotation = 1, ByInterface = 2;

    @AutoLogger
    ILogger logger;

    Map<Method, List<IMethodInterceptHandler>> methodSetMap = new ConcurrentHashMap<>(64);

    @Around("(annotationed()||interfaced())&&!@annotation(tbs.framework.base.annotations.NoInterceptMethod))")
    public Object handleAnnoationed(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        List<IMethodInterceptHandler> methodInterceptHandlers = getMethodHandlers(joinPoint, signature);

        if (CollUtil.isEmpty(methodInterceptHandlers)) {
            logger.debug("no handler :{}", signature.getMethod().getName());
            return joinPoint.proceed();
        }
        IMethodInterceptHandler.HandleReturnedResult result = IMethodInterceptHandler.HandleReturnedResult.result(null);
        Object[] args = joinPoint.getArgs();
        try {
            for (IMethodInterceptHandler handler : methodInterceptHandlers) {
                handler.handleArgs(joinPoint.getTarget(), signature.getMethod(), args);
            }
            result.setResult(joinPoint.proceed(args));
            for (IMethodInterceptHandler handler : methodInterceptHandlers) {
                result = handler.handleReturn(joinPoint.getTarget(), signature.getMethod(), result.getResult());
                if (result.isFinal()) {
                    break;
                }
            }
        } catch (Throwable throwable) {
            for (IMethodInterceptHandler handler : methodInterceptHandlers) {
                handler.handleException(throwable, joinPoint.getTarget(), signature.getMethod(), result, args);
                if (result.getError() != null) {
                    throw result.getError();
                }
            }
        }
        return result.getResult();
    }

    private List<IMethodInterceptHandler> getHandlersByAnnoations(ProceedingJoinPoint joinPoint,
        MethodSignature signature, Method method) {
        Set<MethodIntercept> an =
            AnnotatedElementUtils.getAllMergedAnnotations(signature.getMethod(), MethodIntercept.class);
        if (CollUtil.isEmpty(an)) {
            return new ArrayList<>(0);
        } else {
            Set<IMethodInterceptHandler> set = new HashSet<>();
            for (MethodIntercept methodIntercept : an) {
                for (Class<? extends IMethodInterceptHandler> m : methodIntercept.value()) {
                    try {
                        set.add(SpringUtil.getBean(m));
                    } catch (Exception e) {
                        logger.error(e, "error when get bean from spring context");
                    }
                }
            }
            return set.stream().sorted(MethodInterceptAop::compareMethodInterceptHandler).collect(Collectors.toList());

        }
    }

    private List<IMethodInterceptHandler> getHandlersByInterfaces(ProceedingJoinPoint joinPoint,
        MethodSignature signature, Method m) {
        IInterceptedTarget target = (IInterceptedTarget)joinPoint.getTarget();
        return target.handlers();
    }

    private List<IMethodInterceptHandler> getMethodHandlers(ProceedingJoinPoint joinPoint, MethodSignature signature) {

        return methodSetMap.computeIfAbsent(signature.getMethod(), (p) -> {
            int tp = ByAnnotation;
            Class<?> targetClass = signature.getMethod().getDeclaringClass();
            if (BeanUtil.iSBaseFrom(targetClass, IInterceptedTarget.class)) {
                tp = ByInterface;
            }
            switch (tp) {
                case ByAnnotation:
                    return getHandlersByAnnoations(joinPoint, signature, p);
                case ByInterface:
                    return getHandlersByInterfaces(joinPoint, signature, p);
                default:
                    return new ArrayList<>(0);
            }

        });
    }

    private static final int compareMethodInterceptHandler(IMethodInterceptHandler o1, IMethodInterceptHandler o2) {
        return o1.getOrder() - o2.getOrder();
    }
}
