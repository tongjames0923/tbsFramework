package tbs.framework.multilingual.annotations;

import tbs.framework.multilingual.ILocal;
import tbs.framework.multilingual.ITranslationParameters;
import tbs.framework.multilingual.impls.LocalStringTranslateImpl;
import tbs.framework.multilingual.impls.parameters.NoParameter;

import java.lang.annotation.*;

/**
 * <p>TranslateField class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslateField {
    Class<? extends ILocal> value() default LocalStringTranslateImpl.class;

    Class<? extends ITranslationParameters> args() default NoParameter.class;
}
