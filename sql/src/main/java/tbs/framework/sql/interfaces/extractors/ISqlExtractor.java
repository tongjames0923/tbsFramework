package tbs.framework.sql.interfaces.extractors;

import lombok.Data;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.enums.QueryOrderEnum;
import tbs.framework.sql.interfaces.IQuery;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author abstergo
 */
public interface ISqlExtractor {

    @Data
    public static class SqlInfo {
        private String sql;
        private boolean isAnd = false;
    }

    @Data
    public static class OrderInfo {
        private String field;
        private QueryOrderEnum order;
    }

    Set<SqlInfo> singleWhereSql(Set<QueryField> queryOrderFields, Field field, Object value);

    String assmeblyWhereSql(Map<Field, Set<SqlInfo>> all);

    OrderInfo orderSql(IQuery object, Field field);

    String assmeblyOrderSql(List<OrderInfo> orderInfos);
}
