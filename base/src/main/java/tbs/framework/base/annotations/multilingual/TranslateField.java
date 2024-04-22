package tbs.framework.base.annotations.multilingual;

import tbs.framework.base.multilingaul.ILocal;
import tbs.framework.base.multilingaul.impls.LocalStringTranslateImpl;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateField {
    Class<? extends ILocal> value() default LocalStringTranslateImpl.class;

    String remark() default "";
}
