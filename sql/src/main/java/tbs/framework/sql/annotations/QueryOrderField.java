package tbs.framework.sql.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.sql.constants.OrderConstant;

import java.lang.annotation.*;

/**
 * 字段排序顺序标记
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
@Component
public @interface QueryOrderField {

    String mappedName() default "";

    /**
     * Order query order enum.
     *
     * @return the query order enum
     */
    String order() default OrderConstant.ASC;

}
