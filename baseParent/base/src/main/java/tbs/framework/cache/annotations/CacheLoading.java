package tbs.framework.cache.annotations;

import org.intellij.lang.annotations.Language;
import tbs.framework.cache.ICacheBroker;
import tbs.framework.cache.impls.broker.NoneNullCacheBroker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface CacheLoading {
    @Language("SpEL") String key();

    Class<? extends ICacheBroker> cacheBroker() default NoneNullCacheBroker.class;

    int[] intArgs() default {};

    String[] stringArgs() default {};
}
