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

    /**
     * 日志工厂bean 名称，若为空则使用默认日志工厂
     *
     * @return
     */
    String factory() default "";
}
