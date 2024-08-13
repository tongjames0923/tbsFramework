package tbs.framework.sql.utils;

import cn.hutool.core.collection.CollUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.annotations.QueryFields;
import tbs.framework.sql.annotations.QueryIndex;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.extractors.ISqlExtractor;

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

    ISqlExtractor whereSqlExtractor;

    public QueryUtil(ISqlExtractor whereSqlExtractor) {
        this.whereSqlExtractor = whereSqlExtractor;
    }

    public String getQuery(final IQuery queryObject) {
        final StringBuilder query = new StringBuilder();
        query.append(queryObject.baseQuerySql());

        final List<Field> fields = QueryUtil.getFieldList(queryObject);
        final StringBuilder orderString = new StringBuilder();
        Map<Field, Set<ISqlExtractor.SqlInfo>> all = new HashMap<>(fields.size());
        List<ISqlExtractor.OrderInfo> orderInfos = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            field.setAccessible(true);
            final Set<QueryField> anns = QueryUtil.getAnnotations(field);
            try {
                Set<ISqlExtractor.SqlInfo> sqlInfos =
                    whereSqlExtractor.singleWhereSql(anns, field, field.get(queryObject));
                if (!CollUtil.isEmpty(sqlInfos)) {
                    all.put(field, sqlInfos);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ISqlExtractor.OrderInfo info = whereSqlExtractor.orderSql(queryObject, field);
            if (info != null) {
                orderInfos.add(info);
            }
        }
        String whereSql = whereSqlExtractor.assmeblyWhereSql(all);
        orderString.append(whereSqlExtractor.assmeblyOrderSql(orderInfos));
        QueryUtil.assmebly(whereSql, query, orderString);
        return query.toString();
    }

    private static void assmebly(final String totalWhereSql, final StringBuilder query,
        final StringBuilder orderString) {
        query.append(totalWhereSql).append(orderString.toString());
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

    private static Set<QueryField> getAnnotations(final Field field) {
        final Set<QueryFields> set1 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryFields.class);
        final Set<QueryField> set2 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryField.class);
        for (final QueryFields queryField : set1) {
            set2.addAll(Arrays.asList(queryField.value()));
        }
        return set2;
    }
}
