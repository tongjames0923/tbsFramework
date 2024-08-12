package tbs.framework.sql.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.sql.annotations.*;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.enums.QueryOrderEnum;
import tbs.framework.sql.interfaces.AbstractConvertChainProvider;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.IQueryOrderBy;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 复杂条件查询工具
 *
 * @author abstergo
 */
public class QueryUtil {

    @AutoLogger
    ILogger logger;

    public QueryUtil() {
    }

    public String getQuery(final IQuery queryObject) {
        final StringBuilder query = new StringBuilder();
        query.append(queryObject.baseQuerySql());

        final List<Field> fields = QueryUtil.getFieldList(queryObject);
        final List<Pair<Boolean, String>> totalWhereSql = new LinkedList<>();
        final StringBuilder orderString = new StringBuilder();
        for (final Field field : fields) {
            field.setAccessible(true);
            final Set<QueryField> anns = QueryUtil.getAnnotations(field);
            final List<QueryField> list = anns.stream().sorted((o1, o2) -> {
                return o1.index() - o2.index();
            }).collect(Collectors.toList());

            final StringBuilder whereSql = new StringBuilder();
            final List<Pair<String, String>> l = extractedSql(queryObject, field, list, orderString);
            if (l.isEmpty()) {
                continue;
            }
            final Pair<String, String> fir = l.get(0);
            whereSql.append(fir.getKey());
            for (int i = 1; i < l.size(); i++) {
                final Pair<String, String> pair = l.get(i);
                whereSql.append(pair.getValue()).append(" ").append(pair.getKey());
            }

            final boolean orField = null != field.getDeclaredAnnotation(OrField.class);
            totalWhereSql.add(new Pair<>(orField, String.format("(%s)", whereSql)));
        }
        QueryUtil.assmebly(totalWhereSql, query, orderString);
        return query.toString();
    }

    private static void assmebly(final List<Pair<Boolean, String>> totalWhereSql, final StringBuilder query,
        final StringBuilder orderString) {
        final StringBuilder whereSql = new StringBuilder();
        if (!totalWhereSql.isEmpty()) {
            whereSql.append(" WHERE ");
            final Pair<Boolean, String> fp = totalWhereSql.get(0);
            if (fp.getKey()) {
                whereSql.append("(").append(fp.getValue()).append(")");
            } else {
                whereSql.append("(").append(fp.getValue()).append(")");
            }

            for (int i = 1; i < totalWhereSql.size(); i++) {
                final Pair<Boolean, String> pair = totalWhereSql.get(i);
                if (pair.getKey()) {
                    whereSql.append(" OR (").append(pair.getValue()).append(")");
                } else {
                    whereSql.append(" AND  (").append(pair.getValue()).append(")");
                }
            }

        }

        query.append(whereSql).append(orderString.toString());
    }

    private static List<Field> getFieldList(final IQuery queryObject) {
        final Class<?> clazz = queryObject.getClass();
        final List<Field> fields =
            new ArrayList<>(List.of(clazz.getDeclaredFields())).stream().sorted(new Comparator<Field>() {
                @Override
                public int compare(final Field o1, final Field o2) {
                    return getFieldIndex(o1) - getFieldIndex(o2);
                }
            }).collect(Collectors.toList());
        return fields;
    }

    private static int getFieldIndex(final Field o1) {
        final QueryIndex q1 = o1.getDeclaredAnnotation(QueryIndex.class);
        return null == q1 ? 1000 : q1.index();
    }

    /**
     * @param queryObject 数据实体
     * @param field       字段
     * @param list        key:sql语句 value:connector
     * @return
     */
    private List<Pair<String, String>> extractedSql(final IQuery queryObject, final Field field,
        final List<QueryField> list, final StringBuilder orderString) {
        final List<Pair<String, String>> l = new LinkedList<>();
        String name = QueryUtil.getNameField(field, null);
        for (int i = 0; i < list.size(); i++) {
            final QueryField queryField = list.get(i);
            Object value = null;
            final AbstractConvertChainProvider valueMapperChains;
            try {
                value = field.get(queryObject);
                valueMapperChains = SpringUtil.getBean(queryField.valueMapper());
            } catch (final IllegalAccessException e) {
                this.logger.error(e, e.getMessage());
                continue;
            }
            name = QueryUtil.getNameField(field, queryField);
            if (QueryUtil.ignoreNull(queryField, value)) {
                continue;
            }
            value = QueryUtil.ignoreCase(value, queryField);

            final StringBuilder builder = this.makeSingleSql(queryField, name, valueMapperChains, value);
            l.add(new Pair<>(builder.toString(), this.connector(queryField.connector())));
        }
        if (AnnotatedElementUtils.hasAnnotation(field, QueryOrderField.class)) {
            QueryUtil.getFieldOrder(queryObject, field, orderString, name);
        }
        return l;
    }

    private static Object ignoreCase(Object value, final QueryField queryField) {
        if (value instanceof String && queryField.ignoreCase()) {
            value = ((String)value).toLowerCase();
        }
        return value;
    }

    private static void getFieldOrder(IQuery query, final Field field, final StringBuilder orderString,
        final String name) {
        final Set<QueryOrderField> orderFields =
            AnnotatedElementUtils.getAllMergedAnnotations(field, QueryOrderField.class);
        if (!CollUtil.isEmpty(orderFields)) {
            QueryOrderField orderField = orderFields.iterator().next();
            final IQueryOrderBy orderBy = SpringUtil.getBean(orderField.order(), IQueryOrderBy.class);
            final String ord = QueryOrderEnum.DESC == orderBy.orderBy(query, field, name) ? "DESC" : "ASC";
            if (0 < orderString.length()) {
                orderString.append(",").append(name).append(" ").append(ord);
            } else {
                orderString.append(" ORDER BY ").append(name).append(" ").append(ord);
            }
        }
    }

    private static boolean ignoreNull(final QueryField queryField, final Object value) {
        if (queryField.ignoreNull()) {
            if (null == value) {
                return true;
            }
            if (value instanceof String) {
                if (StrUtil.isEmpty(value.toString())) {
                    return true;
                }
            }
            if (value instanceof Iterable) {
                boolean hasNext = ((Iterable<?>)value).iterator().hasNext();
                return !hasNext;
            }
        }
        return false;
    }

    private StringBuilder makeSingleSql(final QueryField queryField, final String name,
        final AbstractConvertChainProvider valueMapper,
        final Object value) {
        final StringBuilder builder = new StringBuilder();
        String mped = AbstractConvertChainProvider.process(valueMapper, value);
        if (QueryContrastEnum.IS_NOT_NULL == queryField.contrast() ||
            QueryContrastEnum.IS_NULL == queryField.contrast()) {
            builder.append(name).append(contrast(queryField.contrast()));
        } else if (QueryContrastEnum.RLIKE == queryField.contrast()) {
            builder.append(name).append(contrast(queryField.contrast())).append("'%").append(mped).append("' ");
        } else if (QueryContrastEnum.LLIKE == queryField.contrast()) {
            builder.append(name).append(contrast(queryField.contrast())).append("'").append(mped).append("%' ");
        } else if (QueryContrastEnum.IN == queryField.contrast() || QueryContrastEnum.NOT_IN == queryField.contrast()) {
            builder.append(name).append(contrast(queryField.contrast())).append("(").append(mped).append(")");
        } else {
            builder.append(name).append(contrast(queryField.contrast())).append("'").append(mped).append("' ");
        }
        return builder;
    }

    private static String getNameField(final Field field, final QueryField queryField) {
        if (queryField == null || StrUtil.isEmpty(queryField.map())) {
            return "`" + field.getName() + "`";
        } else {
            return "`" + queryField.map() + "`";
        }
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

    private String connector(final QueryConnectorEnum connectorEnum) {

        switch (connectorEnum) {
            case AND:
                return " AND ";
            case OR:
                return " OR ";
            default:
                throw new UnsupportedOperationException("不支持的连接符");
        }
    }

    private static Set<QueryField> getAnnotations(final Field field) {
        final Set<QueryFields> set1 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryFields.class);
        final Set<QueryField> set2 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryField.class);
        for (final QueryFields queryField : set1) {
            set2.addAll(Arrays.asList(queryField.value()));
        }
        return set2;
    }
}
