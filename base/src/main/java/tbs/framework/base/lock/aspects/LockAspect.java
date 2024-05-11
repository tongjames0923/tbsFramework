package tbs.framework.base.lock.aspects;

import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import tbs.framework.base.lock.annotations.LockIt;
import tbs.framework.base.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.base.proxy.impls.LockProxy;

import java.util.NoSuchElementException;
import java.util.Optional;

@Aspect
public class LockAspect {

    @Pointcut("@annotation(tbs.framework.base.lock.annotations.LockIt)")
    public void toLock() {

    }

    @Around("toLock()")
    public Object run(final ProceedingJoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        final LockIt lockIt = methodSignature.getMethod().getDeclaredAnnotation(LockIt.class);
        if (null == lockIt) {
            throw new NoSuchElementException("LockIt annotation not present");
        }
        final String lockName = lockIt.proxyImpl();
        final String lockId = lockIt.lockId();
        final LockProxy proxy = SpringUtil.getBean(lockName);
        Optional v = proxy.safeProxy((o -> {
            return joinPoint.proceed();
        }), null, new SimpleLockAddtionalInfo(lockId));

        return v.isPresent() ? v.get() : null;
    }
}
