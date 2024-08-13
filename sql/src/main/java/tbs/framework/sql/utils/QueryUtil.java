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
 * 复杂条件查询工具类，用于构建基于给定查询对象的SQL查询字符串
 */
public class QueryUtil {

    @AutoLogger
    ILogger logger; // 日志记录器

    ISqlExtractor whereSqlExtractor; // SQL提取器，用于生成WHERE子句

    /**
     * 构造函数，初始化SQL提取器
     *
     * @param whereSqlExtractor SQL提取器接口的实现，用于生成WHERE子句
     */
    public QueryUtil(ISqlExtractor whereSqlExtractor) {
        this.whereSqlExtractor = whereSqlExtractor;
    }

    /**
     * 根据提供的查询对象生成SQL查询字符串
     *
     * @param queryObject 查询对象，用于提取查询条件和排序信息
     * @return 构建完成的SQL查询字符串
     */
    public String getQuery(final IQuery queryObject) {
        final StringBuilder query = new StringBuilder();
        query.append(queryObject.baseQuerySql()); // 添加基础查询SQL

        final List<Field> fields = QueryUtil.getFieldList(queryObject);
        final StringBuilder orderString = new StringBuilder();
        Map<Field, Set<ISqlExtractor.SqlInfo>> all = new HashMap<>(fields.size());
        List<ISqlExtractor.OrderInfo> orderInfos = new ArrayList<>(fields.size());

        // 遍历查询对象的所有字段，处理每个字段上的查询和排序注解
        for (final Field field : fields) {
            field.setAccessible(true); // 设置字段可访问
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

        // 组装WHERE子句和ORDER BY子句
        String whereSql = whereSqlExtractor.assmeblyWhereSql(all);
        orderString.append(whereSqlExtractor.assmeblyOrderSql(orderInfos));
        QueryUtil.assmebly(whereSql, query, orderString);
        return query.toString();
    }

    /**
     * 将WHERE子句和ORDER BY子句添加到查询字符串中
     *
     * @param totalWhereSql 完整的WHERE子句
     * @param query         已构建的查询字符串
     * @param orderString   ORDER BY子句
     */
    private static void assmebly(final String totalWhereSql, final StringBuilder query,
        final StringBuilder orderString) {
        query.append(totalWhereSql).append(orderString.toString());
    }

    /**
     * 获取查询对象中所有带查询注解的字段，并按查询索引排序
     *
     * @param queryObject 查询对象
     * @return 排序后的字段列表
     */
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

    /**
     * 获取字段的查询索引值
     *
     * @param field 字段
     * @return 索引值，若未定义则默认为1000
     */
    private static int getFieldIndex(final Field o1) {
        final QueryIndex q1 = o1.getDeclaredAnnotation(QueryIndex.class);
        return null == q1 ? 1000 : q1.index();
    }

    /**
     * 获取字段上的所有QueryField注解
     *
     * @param field 字段
     * @return 注解集合
     */
    private static Set<QueryField> getAnnotations(final Field field) {
        final Set<QueryFields> set1 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryFields.class);
        final Set<QueryField> set2 = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryField.class);
        for (final QueryFields queryField : set1) {
            set2.addAll(Arrays.asList(queryField.value()));
        }
        return set2;
    }
}
