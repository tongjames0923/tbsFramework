package tbs.framework.multilingual;

import java.util.Locale;

/**
 * <p>ITranslationParameters interface.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public interface ITranslationParameters {
    /**
     * 获取翻译参数
     *
     * @param code   翻译代号
     * @param locale 本地化数据
     * @param source a {@link java.lang.Object} object
     * @return 参数
     */
    Object[] getParameters(String code, Locale locale, Object source);
}
