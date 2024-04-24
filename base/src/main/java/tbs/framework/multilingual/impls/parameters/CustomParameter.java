package tbs.framework.multilingual.impls.parameters;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.cache.ICacheService;
import tbs.framework.multilingual.ITranslationParameters;

import java.util.Locale;

public class CustomParameter implements ITranslationParameters {

    private static ICacheService cacheService = null;

    private static String keyGen(String code) {
        return String.format(String.format("LOCALE_PARAMETER:%s", code));
    }

    public static void setParameter(String code, Object[] values) {
        if (cacheService == null) {
            cacheService = SpringUtil.getBean(ICacheService.class);
        }
        String key = keyGen(code);
        cacheService.put(key, values, true);
    }

    public static Object[] getParameter(String code) {
        if (cacheService == null) {
            cacheService = SpringUtil.getBean(ICacheService.class);
        }
        String key = keyGen(code);
        return (Object[])cacheService.get(key, true, 0).orElse(new Object[0]);
    }

    @Override
    public Object[] getParameters(String code, Locale locale, Object source) {
        return CustomParameter.getParameter(code);
    }
}
