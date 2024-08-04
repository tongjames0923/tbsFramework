package tbs.framework.multilingual.impls.parameters;

import tbs.framework.multilingual.ITranslationParameters;
import tbs.framework.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.Locale;

public class TimeParameter implements ITranslationParameters {
    @Override
    public Object[] getParameters(String code, Locale locale, Object source) {
        LocalDateTime dateTime = LocalDateTime.now();
        return new Object[] {dateTime.format(TimeUtil.DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss)};
    }
}
