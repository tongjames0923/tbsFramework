package tbs.framework.sql.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.sql.constants.OrderConstant;

import java.lang.annotation.*;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Component
@QueryOrderField(order = OrderConstant.ORDERED_MODEL)
public @interface QueryOrderModel {
}
