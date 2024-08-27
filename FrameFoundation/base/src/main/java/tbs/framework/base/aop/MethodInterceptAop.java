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
import tbs.framework.base.interfaces.IMethodInterceptHandler;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

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
    public void intercept() {
    }

    @AutoLogger
    ILogger logger;

    Map<Method, List<IMethodInterceptHandler>> methodSetMap = new ConcurrentHashMap<>(64);

    @Around("intercept()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {

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
                handler.handleException(throwable, joinPoint.getTarget(), signature.getMethod(), args);
            }
            result.setError(throwable);
        }
        return result.getResult();
    }

    private List<IMethodInterceptHandler> getMethodHandlers(ProceedingJoinPoint joinPoint, MethodSignature signature) {

        return methodSetMap.computeIfAbsent(signature.getMethod(), (p) -> {
            Set<MethodIntercept> an =
                AnnotatedElementUtils.getAllMergedAnnotations(joinPoint.getTarget().getClass(), MethodIntercept.class);
            an.addAll(AnnotatedElementUtils.getAllMergedAnnotations(signature.getMethod(), MethodIntercept.class));
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
                return set.stream().sorted(new Comparator<IMethodInterceptHandler>() {

                    @Override
                    public int compare(IMethodInterceptHandler o1, IMethodInterceptHandler o2) {
                        return o1.getOrder() - o2.getOrder();
                    }
                }).collect(Collectors.toList());
            }
        });
    }
}
