package tbs.framework.sql.interfaces.impls;

import tbs.framework.sql.interfaces.IValueMapper;

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
        return value.toString();
    }
}
