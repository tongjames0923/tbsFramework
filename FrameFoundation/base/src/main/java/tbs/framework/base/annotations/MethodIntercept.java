package tbs.framework.base.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.base.interfaces.IMethodInterceptHandler;

import java.lang.annotation.*;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
@Component
public @interface MethodIntercept {
    Class<? extends IMethodInterceptHandler>[] value() default {};
}
