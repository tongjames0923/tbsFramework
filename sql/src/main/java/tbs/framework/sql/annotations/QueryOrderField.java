package tbs.framework.sql.annotations;

import tbs.framework.sql.enums.QueryOrderEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface QueryOrderField {
    QueryOrderEnum order() default QueryOrderEnum.ASC;
}
