package tbs.framework.multilingual.impls.parameters;

import tbs.framework.multilingual.ITranslationParameters;

import java.util.Locale;

/**
 * 无参数
 */
public class NoParameter implements ITranslationParameters {
    @Override
    public Object[] getParameters(String code, Locale locale, Object source) {
        return new Object[0];
    }
}
