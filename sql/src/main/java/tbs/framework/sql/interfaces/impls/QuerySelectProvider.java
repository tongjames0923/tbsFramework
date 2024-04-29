package tbs.framework.sql.interfaces.impls;

import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.sql.interfaces.IPage;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.utils.QueryUtil;

public class QuerySelectProvider {

    public String dynamicSql(final IQuery query, final IPage page) {
        final QueryUtil queryUtil = SpringUtil.getBean(QueryUtil.class);
        String q = queryUtil.getQuery(query);
        if (null != page) {
            q += query.pageQuerySql(page);
        }
        return q;
    }
}
