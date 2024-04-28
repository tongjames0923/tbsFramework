package tbs.framework.multilingual.impls;

import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;
import tbs.framework.multilingual.ILocal;
import tbs.framework.multilingual.ITranslationParameters;

import java.util.Locale;

/**
 * <p>LocalStringTranslateImpl class.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public class LocalStringTranslateImpl implements ILocal {

    private final ILogger log;

    private final MultilingualUtil multilingualUtil;

    /**
     * <p>Constructor for LocalStringTranslateImpl.</p>
     *
     * @param logUtil a {@link tbs.framework.base.utils.LogUtil} object
     * @param util a {@link tbs.framework.base.utils.MultilingualUtil} object
     */
    public LocalStringTranslateImpl(LogUtil logUtil, MultilingualUtil util) {
        log = logUtil.getLogger(LocalStringTranslateImpl.class.getName());
        multilingualUtil = util;
    }

    /** {@inheritDoc} */
    @Override
    public Object translate(Object value, ITranslationParameters parameters, Locale lang) {
        return multilingualUtil.translate(value,
            null == parameters ? new Object[] {} : parameters.getParameters(value.toString(), lang, value), lang);
    }

    /** {@inheritDoc} */
    @Override
    public boolean typeSupport(Class<?> type) {
        return type.equals(String.class);
    }
}
