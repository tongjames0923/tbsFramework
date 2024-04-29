package tbs.framework.sql.utils;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.sql.annotations.*;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.enums.QueryOrderEnum;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.IValueMapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class QueryUtil {

    ILogger logger;

    public QueryUtil(LogUtil logUtil) {
        logger = logUtil.getLogger(QueryUtil.class.getName());
    }

    public String getQuery(IQuery queryObject) {
        StringBuilder query = new StringBuilder();
        query.append(queryObject.baseQuerySql());

        List<Field> fields = getFieldList(queryObject);
        List<Pair<Boolean, String>> totalWhereSql = new LinkedList<>();
        StringBuilder orderString = new StringBuilder();
        for (Field field : fields) {
            field.setAccessible(true);
            Set<QueryField> anns = getAnnotations(field);
            List<QueryField> list = anns.stream().sorted((o1, o2) -> {
                return o1.index() - o2.index();
            }).collect(Collectors.toList());

            StringBuilder whereSql = new StringBuilder();
            List<Pair<String, String>> l = this.extractedSql(queryObject, field, list, orderString);
            if (l.isEmpty()) {
                continue;
            }
            Pair<String, String> fir = l.get(0);
            whereSql.append(fir.getKey());
            for (int i = 1; i < l.size(); i++) {
                Pair<String, String> pair = l.get(i);
                whereSql.append(pair.getValue()).append(" ").append(pair.getKey());
            }

            boolean orField = null != field.getDeclaredAnnotation(OrField.class);
            totalWhereSql.add(new Pair<>(orField, String.format("(%s)", whereSql)));
        }
        assmebly(totalWhereSql, query, orderString);
        return query.toString();
    }

    private static void assmebly(List<Pair<Boolean, String>> totalWhereSql, StringBuilder query,
        StringBuilder orderString) {
        StringBuilder whereSql = new StringBuilder();
        if (!totalWhereSql.isEmpty()) {
            whereSql.append(" WHERE ");
            Pair<Boolean, String> fp = totalWhereSql.get(0);
            if (fp.getKey()) {
                whereSql.append("(").append(fp.getValue()).append(")");
            } else {
                whereSql.append("(").append(fp.getValue()).append(")");
            }

            for (int i = 1; i < totalWhereSql.size(); i++) {
                Pair<Boolean, String> pair = totalWhereSql.get(i);
                if (pair.getKey()) {
                    whereSql.append(" OR (").append(pair.getValue()).append(")");
                } else {
                    whereSql.append(" AND  (").append(pair.getValue()).append(")");
                }
            }

        }

        query.append(whereSql).append(orderString);
    }

    private static List<Field> getFieldList(IQuery queryObject) {
        Class<?> clazz = queryObject.getClass();
        List<Field> fields =
            new ArrayList<>(List.of(clazz.getDeclaredFields())).stream().sorted(new Comparator<Field>() {
                @Override
                public int compare(Field o1, Field o2) {
                    return QueryUtil.getFieldIndex(o1) - QueryUtil.getFieldIndex(o2);
                }
            }).collect(Collectors.toList());
        return fields;
    }

    private static int getFieldIndex(Field o1) {
        QueryIndex q1 = o1.getDeclaredAnnotation(QueryIndex.class);
        return q1 == null ? 1000 : q1.index();
    }

    /**
     * @param queryObject 数据实体
     * @param field       字段
     * @param list        key:sql语句 value:connector
     * @return
     */
    private List<Pair<String, String>> extractedSql(IQuery queryObject, Field field, List<QueryField> list,
        StringBuilder orderString) {
        List<Pair<String, String>> l = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            QueryField queryField = list.get(i);
            Object value = null;
            IValueMapper valueMapper;
            try {
                value = field.get(queryObject);
                valueMapper = SpringUtil.getBean(queryField.valueMapper());
            } catch (IllegalAccessException e) {
                logger.error(e, e.getMessage());
                continue;
            }
            if (ignoreNull(queryField, value)) {
                continue;
            }
            String name = getNameField(field, queryField);
            getFieldOrder(field, orderString, name);
            value = ignoreCase(value, queryField);

            StringBuilder builder = makeSingleSql(queryField, name, valueMapper, value);
            l.add(new Pair<>(builder.toString(), this.connector(queryField.connector())));
        }
        return l;
    }

    private static Object ignoreCase(Object value, QueryField queryField) {
        if (value instanceof String && queryField.ignoreCase()) {
            value = ((String)value).toLowerCase();
        }
        return value;
    }

    private static void getFieldOrder(Field field, StringBuilder orderString, String name) {
        QueryOrderField orderField = field.getDeclaredAnnotation(QueryOrderField.class);
        if (orderField != null) {
            String ord = orderField.order() == QueryOrderEnum.DESC ? "DESC" : "ASC";
            if (orderString.length() > 0) {
                orderString.append(",").append(name).append(" ").append(ord);
            } else {
                orderString.append(" ORDER BY ").append(name).append(" ").append(ord);
            }
        }
    }

    private static boolean ignoreNull(QueryField queryField, Object value) {
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
                final boolean hasNext = ((Iterable<?>)value).iterator().hasNext();
                return !hasNext;
            }
        }
        return false;
    }

    private StringBuilder makeSingleSql(QueryField queryField, String name, IValueMapper valueMapper, Object value) {
        StringBuilder builder = new StringBuilder();
        if (QueryContrastEnum.IS_NOT_NULL == queryField.contrast() ||
            QueryContrastEnum.IS_NULL == queryField.contrast()) {
            builder.append(name).append(this.contrast(queryField.contrast()));
        } else if (QueryContrastEnum.RLIKE == queryField.contrast()) {
            builder.append(name).append(this.contrast(queryField.contrast())).append("'%")
                .append(valueMapper.map(value)).append("' ");
        } else if (QueryContrastEnum.LLIKE == queryField.contrast()) {
            builder.append(name).append(this.contrast(queryField.contrast())).append("'").append(valueMapper.map(value))
                .append("%' ");
        } else if (QueryContrastEnum.IN == queryField.contrast() || QueryContrastEnum.NOT_IN == queryField.contrast()) {
            builder.append(name).append(this.contrast(queryField.contrast())).append("(").append(valueMapper.map(value))
                .append(")");
        } else {
            builder.append(name).append(this.contrast(queryField.contrast())).append("'").append(valueMapper.map(value))
                .append("' ");
        }
        return builder;
    }

    private static String getNameField(Field field, QueryField queryField) {
        String name = StrUtil.isEmpty(queryField.map()) ? field.getName() : queryField.map();
        name = String.format("`%s`", name);
        return name;
    }

    private String contrast(QueryContrastEnum queryContrastEnum) {
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

    private String connector(QueryConnectorEnum connectorEnum) {

        switch (connectorEnum) {
            case AND:
                return " AND ";
            case OR:
                return " OR ";
            default:
                throw new UnsupportedOperationException("不支持的连接符");
        }
    }

    private static Set<QueryField> getAnnotations(Field field) {
        Set<QueryFields> set1 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryFields.class);
        Set<QueryField> set2 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryField.class);
        for (QueryFields queryField : set1) {
            set2.addAll(Arrays.asList(queryField.value()));
        }
        return set2;
    }
}
