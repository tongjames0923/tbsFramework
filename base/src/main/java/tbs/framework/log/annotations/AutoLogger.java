package tbs.framework.log.annotations;

import java.lang.annotation.*;

/**
 * 自动代理装配Ilogger
 *
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
public @interface AutoLogger {
    /**
     * logger name,为空则装配当前类名
     *
     * @return
     */
    String value() default "";
}
