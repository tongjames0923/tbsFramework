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

    private static String keyGen(String code) {
        return String.format(String.format("LOCALE_PARAMETER:%s", code));
    }

    /**
     * <p>setParameter.</p>
     *
     * @param code a {@link java.lang.String} object
     * @param values an array of {@link java.lang.Object} objects
     */
    public static void setParameter(String code, Object[] values) {
        if (null == CustomParameter.cacheService) {
            cacheService = SpringUtil.getBean(ICacheService.class);
        }
        String key = keyGen(code);
        cacheService.put(key, values, true);
    }

    /**
     * <p>getParameter.</p>
     *
     * @param code a {@link java.lang.String} object
     * @return an array of {@link java.lang.Object} objects
     */
    public static Object[] getParameter(String code) {
        if (null == CustomParameter.cacheService) {
            cacheService = SpringUtil.getBean(ICacheService.class);
        }
        String key = keyGen(code);
        return (Object[])cacheService.get(key, true, 0).orElse(new Object[0]);
    }

    /** {@inheritDoc} */
    @Override
    public Object[] getParameters(String code, Locale locale, Object source) {
        return CustomParameter.getParameter(code);
    }
}
