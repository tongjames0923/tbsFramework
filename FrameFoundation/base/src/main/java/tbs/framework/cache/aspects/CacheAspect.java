package tbs.framework.cache.aspects;

import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Lazy;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import tbs.framework.cache.AbstractTimeBaseCacheEliminationStrategy;
import tbs.framework.cache.AbstractTimeBaseCacheManager;
import tbs.framework.cache.annotations.CacheLoading;
import tbs.framework.cache.annotations.CacheUnloading;
import tbs.framework.cache.properties.CacheProperty;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.proxy.impls.LockProxy;

import javax.annotation.Resource;
import java.util.Optional;

@Aspect
public class CacheAspect {

    @Resource
    @Lazy
    AbstractTimeBaseCacheManager cacheService;

    @Resource
    @Lazy
    LockProxy lockProxy;

    @Resource
    CacheProperty cacheProperty;

    private static final String CacheLockPrefix = "CacheLock.ASPECT::";

    @Pointcut(
        "@annotation(tbs.framework.cache.annotations.CacheLoading)||@annotation(tbs.framework.cache.annotations.CacheUnloading)")
    public void cache() {

    }

    public String getKey(String k, MethodSignature methodSignature, Object[] args) {
        // 创建spel表达式分析器
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("args", args);
        // 输入表达式
        Expression exp = parser.parseExpression(k);
        // 获取表达式的输出结果，getValue入参是返回参数的类型
        return "CACHE_ASPECT:" +
            methodSignature.getDeclaringTypeName() +
            "." +
            methodSignature.getName() +
            ":" +
            exp.getValue(context, String.class);
    }

    @Around("cache()")
    public Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Object result = cacheLoad(joinPoint, methodSignature);
        unCache(joinPoint, methodSignature);
        return result;
    }

    private void unCache(ProceedingJoinPoint pjp, MethodSignature methodSignature) throws Throwable {
        CacheUnloading annotation = methodSignature.getMethod().getDeclaredAnnotation(CacheUnloading.class);
        if (annotation == null) {
            return;
        }

        AbstractTimeBaseCacheEliminationStrategy eliminationStrategy =
            SpringUtil.getBean(annotation.cacheKillStrategy());
        String key = getKey(annotation.key(), methodSignature, pjp.getArgs());
        lockProxy.proxy((p) -> {
            eliminationStrategy.judgeAndClean(cacheService, SpringUtil.getBean(cacheProperty.getCacheKillJudgeMaker())
                .makeJudge(key, annotation.intArgs(), annotation.stringArgs()));
            return null;
        }, null, new SimpleLockAddtionalInfo(CacheLockPrefix + key));

    }

    private Object cacheLoad(ProceedingJoinPoint joinPoint, MethodSignature methodSignature) throws Throwable {
        CacheLoading cacheLoading = methodSignature.getMethod().getDeclaredAnnotation(CacheLoading.class);
        if (cacheLoading == null) {
            return Optional.ofNullable(joinPoint.proceed());
        }
        String key = getKey(cacheLoading.key(), methodSignature, joinPoint.getArgs());
        Optional result = lockProxy.proxy((p) -> {
            boolean hasCache = cacheService.exists(key);
            if (hasCache) {
                return cacheService.get(key);
            } else {
                Object res = joinPoint.proceed();
                if (res == null) {
                    if (cacheProperty.isAcceptNullValues()) {
                        cacheService.put(key, res, true);
                    }
                } else {
                    cacheService.put(key, res, true);
                }

                return res;
            }
        }, null, new SimpleLockAddtionalInfo(CacheLockPrefix + key));

        return result.isEmpty() ? null : result.get();
    }

}
