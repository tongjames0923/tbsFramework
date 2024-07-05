package tbs.framework.multilingual.impls.parameters;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.cache.impls.managers.ImportedTimeBaseCacheManager;
import tbs.framework.multilingual.ITranslationParameters;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

/**
 * <p>CustomParameter class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class CustomParameter implements ITranslationParameters {

    private static ImportedTimeBaseCacheManager cacheService;

    private static String keyGen(final String code) {
        return String.format(String.format("LOCALE_PARAMETER:%s", code));
    }

    /**
     * <p>setParameter.</p>
     *
     * @param code   a {@link java.lang.String} object
     * @param values an array of {@link java.lang.Object} objects
     */
    public static void setParameter(final String code, final Object[] values) {
        if (null == cacheService) {
            CustomParameter.cacheService = SpringUtil.getBean(ImportedTimeBaseCacheManager.class);
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
            CustomParameter.cacheService = SpringUtil.getBean(ImportedTimeBaseCacheManager.class);
        }
        final String key = CustomParameter.keyGen(code);
        return (Object[])Optional.ofNullable(CustomParameter.cacheService.getAndRemove(key, Duration.ofMillis(0)))
            .orElse(new Object[0]);
    }

    @Override
    public Object[] getParameters(final String code, final Locale locale, final Object source) {
        return getParameter(code);
    }
}
