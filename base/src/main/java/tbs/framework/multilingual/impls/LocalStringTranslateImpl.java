package tbs.framework.multilingual.impls;

import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;
import tbs.framework.multilingual.ILocal;
import tbs.framework.multilingual.ITranslationParameters;

import java.util.Locale;

public class LocalStringTranslateImpl implements ILocal {

    private final ILogger log;

    private final MultilingualUtil multilingualUtil;

    public LocalStringTranslateImpl(LogUtil logUtil, MultilingualUtil util) {
        log = logUtil.getLogger(LocalStringTranslateImpl.class.getName());
        multilingualUtil = util;
    }

    @Override
    public Object translate(Object value, ITranslationParameters parameters, Locale lang) {
        return multilingualUtil.translate(value,
            parameters == null ? new Object[] {} : parameters.getParameters(value.toString(), lang, value), lang);
    }

    @Override
    public boolean typeSupport(Class<?> type) {
        return type.equals(String.class);
    }
}
