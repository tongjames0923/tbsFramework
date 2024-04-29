package tbs.framework.sql.interfaces.impls;

import tbs.framework.base.utils.TimeUtil;
import tbs.framework.sql.interfaces.IValueMapper;

import java.time.temporal.Temporal;
import java.util.Date;

public class SimpleValueMapper implements IValueMapper {
    @Override
    public String map(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Iterable) {
            StringBuilder sb = new StringBuilder();
            for (Object v : (Iterable<?>)value) {
                sb.append("'").append(v.toString()).append("',");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
        if (value instanceof Temporal) {
            Temporal instant = (Temporal)value;
            return TimeUtil.DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss.format(instant).toString();
        }
        if (value instanceof Date) {
            Date date = (Date)value;
            return TimeUtil.DATE_FORMAT_yyyyMMddHHmmss.format(date).toString();
        }
        return value.toString();
    }
}
