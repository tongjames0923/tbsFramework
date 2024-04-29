package tbs.framework.sql.interfaces.impls;

import tbs.framework.sql.interfaces.IValueMapper;

public class SimpleValueMapper implements IValueMapper {
    @Override
    public String map(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
