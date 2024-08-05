/**
 * TimeParameter 类实现了 ITranslationParameters 接口，用于处理时间相关的翻译参数。
 */
package tbs.framework.multilingual.impls.parameters;

import tbs.framework.multilingual.ITranslationParameters;
import tbs.framework.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * TimeParameter 类实现了 ITranslationParameters 接口，用于处理时间相关的翻译参数。提供yyyyMMddHHmmss 格式的时间参数。
 *
 * @author abstergo
 */
public class TimeParameter implements ITranslationParameters {

    /**
     * 获取翻译参数。
     *
     * @param code   翻译代码
     * @param locale 语言环境
     * @param source 源数据
     * @return 翻译参数数组
     */
    @Override
    public Object[] getParameters(String code, Locale locale, Object source) {
        LocalDateTime dateTime = LocalDateTime.now();
        return new Object[] {dateTime.format(TimeUtil.DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss)};
    }
}

