package tbs.framework.multilingual.impls.parameters;

import tbs.framework.multilingual.ITranslationParameters;

import java.util.Locale;

/**
 * 无参数
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class NoParameter implements ITranslationParameters {
    @Override
    public Object[] getParameters(final String code, final Locale locale, final Object source) {
        return new Object[0];
    }
}
