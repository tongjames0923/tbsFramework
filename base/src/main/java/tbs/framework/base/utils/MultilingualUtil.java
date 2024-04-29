package tbs.framework.base.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import tbs.framework.base.log.ILogger;
import tbs.framework.multilingual.ILocal;
import tbs.framework.multilingual.annotations.TranslateField;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Locale;

public class MultilingualUtil {
    @Resource
    private MessageSource messageSource;

    private final ILogger log;

    public MultilingualUtil(final LogUtil logUtil) {
        this.log = logUtil.getLogger(MultilingualUtil.class.getName());
    }

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public String message(final String code, final Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public String messageOrDefault(final String code, final String defaultVal, final Object... args) {
        try {
            return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (final Exception e) {
            return defaultVal;
        }
    }

    public Object translate(final Object value, final Object[] args, final Locale lang) {
        try {
            return this.messageSource.getMessage(value.toString(), args, lang);
        } catch (final Exception e) {
            this.log.error(e, String.format("local translate fail for %s,msg:%s", value.toString(), e.getMessage()));
            return value;
        }
    }

    public Object translate(final Object data) {
        try {
            for (final Field field : data.getClass().getDeclaredFields()) {
                try {
                    final TranslateField tranlateField = field.getDeclaredAnnotation(TranslateField.class);
                    if (null == tranlateField) {
                        continue;
                    }
                    final ILocal ilocal = SpringUtil.getBean(tranlateField.value());
                    if (null == ilocal) {
                        throw new ClassNotFoundException("不存在的翻译执行器");
                    }
                    if (!ilocal.typeSupport(field.getType())) {
                        throw new UnsupportedOperationException("不支持的翻译类型");
                    }
                    field.setAccessible(true);
                    field.set(data, ilocal.translate(field.get(data), tranlateField.args().newInstance(),
                        LocaleContextHolder.getLocale()));
                } catch (final Exception e) {
                    this.log.error(e, String.format("translate fail msg:%s,filed:%s", e.getMessage(), field.getName()));
                }
            }
        } catch (final Exception e) {
            this.log.error(e, e.getMessage());
        }
        return data;
    }
}
