package tbs.framework.lock.aspects;

import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import tbs.framework.lock.ILock;
import tbs.framework.lock.annotations.LockIt;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.utils.LockUtils;

import java.util.NoSuchElementException;
import java.util.Optional;

@Aspect
public class LockAspect {

    @Pointcut("@annotation(tbs.framework.lock.annotations.LockIt)")
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
        ILock lock = LockUtils.getInstance().getLock(lockId);
        Optional v = proxy.safeProxy((o -> {
            return joinPoint.proceed();
        }), null, new SimpleLockAddtionalInfo(lock));
        lock = null;
        return v.isPresent() ? v.get() : null;
    }
}