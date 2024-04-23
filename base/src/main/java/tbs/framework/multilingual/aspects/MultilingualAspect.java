package tbs.framework.multilingual.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import tbs.framework.base.utils.MultilingualUtil;

import javax.annotation.Resource;

@Aspect
public class MultilingualAspect {
    @Pointcut("@annotation(tbs.framework.multilingual.annotations.Translated)")
    public void multilingualAspect() {
    }

    @Resource
    MultilingualUtil multilingualUtil;

    @Around("multilingualAspect()")
    public Object translateField(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result != null) {
            result = multilingualUtil.translate(result);
        }
        return result;
    }
}
