package tbs.framework.sql.interfaces.impls.convertor;

import tbs.framework.sql.interfaces.AbstractConvertor;
import tbs.framework.utils.TimeUtil;

import java.time.temporal.Temporal;
import java.util.Date;

/**
 * 日期转换 Temporal 和Date
 * @author Abstergo
 */
public class DateValueConvertor extends AbstractConvertor {

    @Override
    protected boolean support(Class<?> t) {
        return Temporal.class.isAssignableFrom(t) || Date.class.isAssignableFrom(t);
    }

    @Override
    protected String doConvert(Object value) {
        if (value instanceof Temporal) {
            final Temporal instant = (Temporal)value;
            return TimeUtil.DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss.format(instant);
        }
        if (value instanceof Date) {
            final Date date = (Date)value;
            return TimeUtil.DATE_FORMAT_yyyyMMddHHmmss.format(date);
        }
        return "";
    }
}
