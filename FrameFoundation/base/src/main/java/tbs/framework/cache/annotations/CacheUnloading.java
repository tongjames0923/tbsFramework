package tbs.framework.cache.annotations;

import org.intellij.lang.annotations.Language;
import tbs.framework.cache.AbstractTimeBaseCacheEliminationStrategy;
import tbs.framework.cache.impls.eliminations.strategys.ExpiredCacheElimination;

import java.lang.annotation.*;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface CacheUnloading {

    @Language("SpEL") String key();

    Class<? extends AbstractTimeBaseCacheEliminationStrategy> cacheKillStrategy() default ExpiredCacheElimination.class;

    int[] intArgs() default {};

    String[] stringArgs() default {};

}
