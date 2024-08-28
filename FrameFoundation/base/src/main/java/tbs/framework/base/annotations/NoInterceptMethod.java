package tbs.framework.base.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Inherited
@Component
public @interface NoInterceptMethod {
}
