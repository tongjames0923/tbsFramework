package tbs.framework.sql.interfaces.mappers;

import org.apache.ibatis.annotations.SelectProvider;
import tbs.framework.sql.interfaces.IPage;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.impls.QuerySelectProvider;

import java.util.List;

public interface QueryMapper<T> {

    @SelectProvider(type = QuerySelectProvider.class, method = "dynamicSql")
    List<T> queryByQO(IQuery query, IPage page);
}
