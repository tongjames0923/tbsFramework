package tbs.framework.sql.interfaces.impls;

import tbs.framework.base.utils.TimeUtil;
import tbs.framework.sql.interfaces.IValueMapper;

import java.time.temporal.Temporal;
import java.util.Date;

public class SimpleValueMapper implements IValueMapper {
    @Override
    public String map(final Object value) {
        if (null == value) {
            return "";
        }
        if (value instanceof Iterable) {
            final StringBuilder sb = new StringBuilder();
            for (final Object v : (Iterable<?>)value) {
                sb.append("'").append(v.toString()).append("',");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
        if (value instanceof Temporal) {
            final Temporal instant = (Temporal)value;
            return TimeUtil.DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss.format(instant);
        }
        if (value instanceof Date) {
            final Date date = (Date)value;
            return TimeUtil.DATE_FORMAT_yyyyMMddHHmmss.format(date);
        }
        return value.toString();
    }
}
