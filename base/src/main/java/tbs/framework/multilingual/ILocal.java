package tbs.framework.multilingual;

import java.util.Locale;

public interface ILocal {
    /**
     * 翻译功能
     *
     * @param value      待翻译的实体
     * @param parameters 备注
     * @param lang       语言
     * @return 结果实体
     */
    Object translate(Object value, ITranslationParameters parameters, Locale lang);

    /**
     * 类型支持
     * @param type
     * @return
     */
    boolean typeSupport(Class<?> type);
}
