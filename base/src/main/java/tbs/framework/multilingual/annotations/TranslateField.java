package tbs.framework.multilingual.annotations;

import tbs.framework.multilingual.ILocal;
import tbs.framework.multilingual.impls.LocalStringTranslateImpl;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateField {
    Class<? extends ILocal> value() default LocalStringTranslateImpl.class;

    String remark() default "";
}
