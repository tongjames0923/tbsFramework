package tbs.framework.base.lock.annotations;

import tbs.framework.base.constants.BeanNameConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface LockIt {
    /**
     * 锁代理的实例名称 spring管理的beanName
     *
     * @return
     */
    String value() default BeanNameConstant.BUILTIN_LOCK_PROXY;

    /**
     * 锁id
     *
     * @return
     */
    String lockId() default "";
}
