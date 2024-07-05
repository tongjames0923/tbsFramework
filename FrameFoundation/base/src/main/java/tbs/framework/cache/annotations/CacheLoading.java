package tbs.framework.cache.annotations;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface CacheLoading {
    @Language("SpEL") String key();
}
