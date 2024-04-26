package tbs.framework.auth.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.auth.interfaces.ICustomPermissionChecker;
import tbs.framework.auth.interfaces.IPermissionProvider;
import tbs.framework.auth.interfaces.impls.permissionCheck.NotCustom;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 接口验证 需要
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Repeatable(PermissionValidateds.class)
public @interface PermissionValidated {
    /**
     * 所需的role,若userPermissionProvider存在则失效
     *
     * @return
     */
    String value() default "";

    /**
     * 自定义权限检验，若NotCustom则简单比较用户数据中的role和value是否对应
     *
     * @return
     */

    Class<? extends ICustomPermissionChecker> customCheck() default NotCustom.class;

    /**
     * 自定义所需权限获取 若NotCustom则采用value
     *
     * @return
     */

    Class<? extends IPermissionProvider> userPermissionProvider() default NotCustom.class;

}
