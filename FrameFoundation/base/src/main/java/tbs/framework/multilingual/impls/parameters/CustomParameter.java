package tbs.framework.multilingual.impls.parameters;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.cache.ICacheService;
import tbs.framework.multilingual.ITranslationParameters;

import java.util.Locale;

/**
 * <p>CustomParameter class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class CustomParameter implements ITranslationParameters {

    private static ICacheService cacheService;

    private static String keyGen(final String code) {
        return String.format(String.format("LOCALE_PARAMETER:%s", code));
    }

    /**
     * <p>setParameter.</p>
     *
     * @param code a {@link java.lang.String} object
     * @param values an array of {@link java.lang.Object} objects
     */
    public static void setParameter(final String code, final Object[] values) {
        if (null == cacheService) {
            CustomParameter.cacheService = SpringUtil.getBean(ICacheService.class);
        }
        final String key = CustomParameter.keyGen(code);
        CustomParameter.cacheService.put(key, values, true);
    }

    /**
     * <p>getParameter.</p>
     *
     * @param code a {@link java.lang.String} object
     * @return an array of {@link java.lang.Object} objects
     */
    public static Object[] getParameter(final String code) {
        if (null == cacheService) {
            CustomParameter.cacheService = SpringUtil.getBean(ICacheService.class);
        }
        final String key = CustomParameter.keyGen(code);
        return (Object[])CustomParameter.cacheService.get(key, true, 0).orElse(new Object[0]);
    }

    @Override
    public Object[] getParameters(final String code, final Locale locale, final Object source) {
        return getParameter(code);
    }
}
