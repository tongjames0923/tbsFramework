package tbs.framework.cache.aspects;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Lazy;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import tbs.framework.cache.annotations.CacheLoading;
import tbs.framework.cache.annotations.CacheUnloading;
import tbs.framework.cache.managers.AbstractCacheManager;
import tbs.framework.cache.properties.CacheProperty;
import tbs.framework.cache.strategy.AbstractCacheEliminationStrategy;
import tbs.framework.expression.ICompilerUnit;
import tbs.framework.expression.impl.compiler.SpelCompiler;
import tbs.framework.lock.impls.SimpleLockAddtionalInfo;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.utils.LockUtils;
import tbs.framework.utils.StrUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Abstergo
 */
@Aspect
public class CacheAspect {

    AbstractCacheManager cacheService;

    @Resource
    @Lazy
    LockProxy lockProxy;

    @Resource
    CacheProperty cacheProperty;

    @AutoLogger
    ILogger logger;

    private static final String CacheLockPrefix = "CacheLock.ASPECT::";

    private Map<String, ICompilerUnit> compilerUnitMap = new ConcurrentHashMap<>(16);

    SpelCompiler spelCompiler = new SpelCompiler();

    @PostConstruct
    public void init() {
        logger.info("in used cache manager:{}", cacheService.toString());
    }

    public CacheAspect(AbstractCacheManager cacheService) {
        this.cacheService = cacheService;
    }

    @Pointcut(
        "@annotation(tbs.framework.cache.annotations.CacheLoading)||@annotation(tbs.framework.cache.annotations.CacheUnloading)")
    public void cache() {

    }

    public String getKey(String k, MethodSignature methodSignature, Object[] args) {
        ICompilerUnit compilerUnit = compilerUnitMap.computeIfAbsent(k, (key) -> spelCompiler.getCompilerUnit(key));
        String cacheKey = "";
        try {
            cacheKey = compilerUnit.exeCompilerUnit(null, args).getResult().toString();
        } catch (Exception e) {
            logger.error(e, "compile error by spel expression:" + k);
            throw new RuntimeException("error to convert key to string by spel expression");
        }
        if (StrUtil.isAllBlank(cacheKey)) {
            logger.error(null, "compile error by spel expression:" + k);
            throw new RuntimeException("create one empty string by spel expression");
        }

        // 获取表达式的输出结果，getValue入参是返回参数的类型
        return "CACHE_ASPECT:" +
            methodSignature.getDeclaringTypeName() +
            "." +
            methodSignature.getName() +
            ":" +
            cacheKey;
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

        AbstractCacheEliminationStrategy eliminationStrategy = SpringUtil.getBean(annotation.cacheKillStrategy());
        String key = getKey(annotation.key(), methodSignature, pjp.getArgs());
        lockProxy.proxy((p) -> {
            eliminationStrategy.judgeAndClean(cacheService, SpringUtil.getBean(cacheProperty.getCacheKillJudgeMaker())
                .makeJudge(key, annotation.intArgs(), annotation.stringArgs()));
            return null;
        }, null, new SimpleLockAddtionalInfo(LockUtils.getInstance().getLock(CacheLockPrefix + key)));

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
                return JSON.parseObject(JSON.toJSONString(cacheService.get(key)), methodSignature.getReturnType());
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
        }, null, new SimpleLockAddtionalInfo(LockUtils.getInstance().getLock(CacheLockPrefix + key)));

        return result.isEmpty() ? null : result.get();
    }

}
