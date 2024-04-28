package tbs.framework.multilingual.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import tbs.framework.base.utils.MultilingualUtil;

import javax.annotation.Resource;

/**
 * <p>MultilingualAspect class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
@Aspect
public class MultilingualAspect {
    /**
     * <p>multilingualAspect.</p>
     */
    @Pointcut("@annotation(tbs.framework.multilingual.annotations.Translated)")
    public void multilingualAspect() {
    }

    @Resource
    MultilingualUtil multilingualUtil;

    /**
     * <p>translateField.</p>
     *
     * @param joinPoint a {@link org.aspectj.lang.ProceedingJoinPoint} object
     * @return a {@link java.lang.Object} object
     * @throws java.lang.Throwable if any.
     */
    @Around("multilingualAspect()")
    public Object translateField(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (null != result) {
            result = multilingualUtil.translate(result);
        }
        return result;
    }
}
