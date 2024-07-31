package tbs.framework.sql.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.sql.enums.QueryOrderEnum;

import java.lang.annotation.*;

/**
 * 字段排序顺序标记
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Component
public @interface QueryOrderField {

    /**
     * Order query order enum.
     *
     * @return the query order enum
     */
    QueryOrderEnum order() default QueryOrderEnum.ASC;
}
