package tbs.framework.multilingual.impls;

import tbs.framework.log.ILogger;
import tbs.framework.utils.LogUtil;
import tbs.framework.utils.MultilingualUtil;
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
     * @param logUtil a {@link LogUtil} object
     * @param util a {@link MultilingualUtil} object
     */
    public LocalStringTranslateImpl(final LogUtil logUtil, final MultilingualUtil util) {
        this.log = logUtil.getLogger(LocalStringTranslateImpl.class.getName());
        this.multilingualUtil = util;
    }

    @Override
    public Object translate(final Object value, final ITranslationParameters parameters, final Locale lang) {
        return this.multilingualUtil.translate(value,
            null == parameters ? new Object[] {} : parameters.getParameters(value.toString(), lang, value), lang);
    }

    @Override
    public boolean typeSupport(final Class<?> type) {
        return type.equals(String.class);
    }
}
