package tbs.framework.base.multilingaul;

import org.springframework.context.MessageSource;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;

import javax.annotation.Resource;
import java.util.Locale;

public class LocalStringTranslateImpl implements ILocal {

    private final ILogger log;

    @Resource
    MessageSource messageSource;

    private final MultilingualUtil multilingualUtil;

    public LocalStringTranslateImpl(LogUtil logUtil, MultilingualUtil util) {
        log = logUtil.getLogger(LocalStringTranslateImpl.class.getName());
        multilingualUtil = util;
    }

    @Override
    public Object translate(Object value, String remark, Locale lang) {
        return multilingualUtil.translate(value, remark, lang);
    }

    @Override
    public boolean typeSupport(Class<?> type) {
        return type.equals(String.class);
    }
}
