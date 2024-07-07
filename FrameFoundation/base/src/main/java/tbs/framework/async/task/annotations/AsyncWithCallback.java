package tbs.framework.async.task.annotations;

import tbs.framework.base.constants.BeanNameConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncWithCallback {
    public String callbackBean() default BeanNameConstant.BUILTIN_ASYNC_TASK_CALLBACK;
}
