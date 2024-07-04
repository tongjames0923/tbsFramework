package tbs.framework.cache.annotations;

import org.intellij.lang.annotations.Language;
import tbs.framework.cache.IEliminationStrategy;
import tbs.framework.cache.impls.eliminate.InstantEliminationStrategy;

import java.lang.annotation.*;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface CacheUnloading {

    @Language("SpEL") String key();

    Class<? extends IEliminationStrategy> eliminationStrategy() default InstantEliminationStrategy.class;

    int[] intArgs() default {};

    String[] stringArgs() default {};

}
