package tbs.framework.sql.interfaces.extractors.impls.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.sql.annotations.OrField;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.annotations.QueryOrderField;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.AbstractConvertChainProvider;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.IQueryOrderBy;
import tbs.framework.sql.interfaces.extractors.ISqlExtractor;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
public class MysqlExatractor implements ISqlExtractor {

    public static final String getColumName(QueryField queryField, @NotNull Field f) {
        if (queryField == null || StrUtil.isEmpty(queryField.map())) {
            return f.getName();
        }
        return queryField.map();
    }

    @Override
    public Set<SqlInfo> singleWhereSql(Set<QueryField> queryOrderFields, Field field, Object value) {
        if (field == null) {
            throw new UnsupportedOperationException("field is null");
        }

        Set<SqlInfo> sqlInfos = new HashSet<>();
        List<QueryField> fields = queryOrderFields.stream().filter((p) -> p != null).distinct().sorted((o1, o2) -> {
            return o1.index() - o2.index();
        }).collect(Collectors.toList());
        if (CollUtil.isEmpty(fields)) {
            return sqlInfos;
        }
        for (QueryField queryField : fields) {
            String name = getColumName(queryField, field);
            AbstractConvertChainProvider convertors = SpringUtil.getBean(queryField.valueMapper());

            String valueStr = AbstractConvertChainProvider.process(convertors, value);

            if (queryField.ignoreNull()) {
                if ((value == null || StrUtil.isEmpty(valueStr))) {
                    continue;
                }
            }

            if (queryField.ignoreCase()) {
                valueStr = valueStr.toLowerCase();
            }
            SqlInfo info = new SqlInfo();
            if (queryField.connector() == QueryConnectorEnum.OR) {
                info.setAnd(false);
            }
            String opStr = contrast(queryField.contrast());

            valueStr = contrastStrSet(queryField, valueStr);
            info.setSql(String.format(" (`%s` %s %s) ", name, opStr, valueStr));
            sqlInfos.add(info);
        }
        return sqlInfos;
    }

    @Override
    public String assmeblyWhereSql(Map<Field, Set<SqlInfo>> all) {
        if (CollUtil.isEmpty(all)) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        boolean first = true;
        for (Map.Entry<Field, Set<SqlInfo>> entry : all.entrySet()) {
            Field f = entry.getKey();
            Set<SqlInfo> single = entry.getValue();
            if (CollUtil.isEmpty(single)) {
                continue;
            }
            if (first) {
                sb.append(" WHERE ");
            }

            if (!first) {
                if (AnnotatedElementUtils.hasAnnotation(f, OrField.class)) {
                    sb.append(" OR ");
                } else {
                    sb.append(" AND ");
                }
            }
            sb.append("(");
            boolean anyFirst = true;
            for (SqlInfo sqlInfo : single) {
                if (!anyFirst) {
                    sb.append(sqlInfo.isAnd() ? " AND " : " OR ");
                }
                sb.append(sqlInfo.getSql());
                anyFirst = false;
            }
            sb.append(" )");
            first = false;
        }
        return sb.toString();
    }

    @Override
    public OrderInfo orderSql(IQuery object, Field field) {

        Set<QueryOrderField> orderFields = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryOrderField.class);
        if (CollUtil.isEmpty(orderFields)) {
            return null;
        }

        QueryOrderField orderField = orderFields.iterator().next();
        OrderInfo info = new OrderInfo();
        IQueryOrderBy queryOrderBy = SpringUtil.getBean(orderField.order());
        String mapped = StrUtil.isEmpty(orderField.mappedName()) ? field.getName() : orderField.mappedName();
        info.setField(mapped);
        info.setOrder(queryOrderBy.orderBy(object, field, mapped));
        return info;
    }

    @Override
    public String assmeblyOrderSql(List<OrderInfo> orderInfos) {
        StringBuilder builder = new StringBuilder();
        if (CollUtil.isEmpty(orderInfos)) {
            return builder.toString();
        }
        builder.append(" ORDER BY ");
        for (OrderInfo info : orderInfos) {
            builder.append(info.getField()).append(" ").append(info.getOrder()).append(",");
        }
        return builder.substring(0, builder.length() - 1);
        
    }

    @Nullable
    private static String contrastStrSet(QueryField queryField, String valueStr) {
        if (QueryContrastEnum.IS_NOT_NULL == queryField.contrast() ||
            QueryContrastEnum.IS_NULL == queryField.contrast()) {
            valueStr = "";
        } else if (QueryContrastEnum.RLIKE == queryField.contrast()) {
            valueStr = "%" + valueStr;
        } else if (QueryContrastEnum.LLIKE == queryField.contrast()) {
            valueStr = valueStr + "%";
        } else if (QueryContrastEnum.IN == queryField.contrast() || QueryContrastEnum.NOT_IN == queryField.contrast()) {
            valueStr = "(" + valueStr + ")";
        }
        if (!StrUtil.isEmpty(valueStr)) {
            valueStr = "'" + valueStr + "'";
        }
        return valueStr;
    }

    private String contrast(final QueryContrastEnum queryContrastEnum) {
        switch (queryContrastEnum) {
            case EQUAL:
                return " = ";
            case LESS:
                return " < ";
            case GREATER:
                return " > ";
            case LESS_EQUAL:
                return " <= ";
            case GREATER_EQUAL:
                return " >= ";
            case RLIKE:
            case LLIKE:
                return " like ";
            case IN:
                return " in ";
            case NOT_IN:
                return " not in ";
            case NOT_LIKE:
                return " not like ";
            case IS_NULL:
                return " is null ";
            case IS_NOT_NULL:
                return " is not null ";
            case NOT_EQUAL:
                return " <> ";
            default:
                throw new UnsupportedOperationException("不支持的运算符");
        }
    }
}
