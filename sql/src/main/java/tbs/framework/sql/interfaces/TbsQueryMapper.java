package tbs.framework.sql.interfaces;

import org.apache.ibatis.annotations.SelectProvider;
import tbs.framework.sql.interfaces.impls.QuerySelectProvider;

import java.util.List;

public interface TbsQueryMapper<T> {

    @SelectProvider(type = QuerySelectProvider.class, method = "dynamicSql")
    List<T> queryByQO(IQuery query, IPage page);
}
