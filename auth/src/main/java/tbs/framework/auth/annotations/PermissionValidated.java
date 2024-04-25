package tbs.framework.auth.annotations;

import org.springframework.stereotype.Component;
import tbs.framework.auth.interfaces.impls.permissionCheck.NotCustom;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.BiFunction;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Repeatable(PermissionValidateds.class)
public @interface PermissionValidated {
    String value();

    Class<? extends BiFunction<PermissionModel, UserModel, PermissionModel.VerificationResult>> customCheck() default NotCustom.class;
}
