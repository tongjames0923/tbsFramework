package tbs.framework.base.lock.aspects;

import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import tbs.framework.base.lock.annotations.LockIt;
import tbs.framework.base.proxy.impls.LockProxy;

@Aspect
public class LockAspect {

    @Pointcut("@annotation(tbs.framework.base.lock.annotations.LockIt)")
    public void toLock() {

    }

    @Around("toLock()")
    public Object run(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        LockIt lockIt = methodSignature.getMethod().getDeclaredAnnotation(LockIt.class);
        if (null == lockIt) {
            throw new RuntimeException("LockIt annotation not present");
        }
        String lockName = lockIt.value();
        LockProxy proxy = SpringUtil.getBean(lockName);
        return proxy.safeProxy((o -> {
            return joinPoint.proceed();
        }), null);
    }
}
