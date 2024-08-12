package tbs.framework.sql.interfaces.impls.orders;

import tbs.framework.sql.enums.QueryOrderEnum;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.IQueryOrderBy;

import java.lang.reflect.Field;


public class AscStaticOrder implements IQueryOrderBy {
    @Override
    public QueryOrderEnum orderBy(IQuery target, Field field, String mappedName) {
        return QueryOrderEnum.ASC;
    }
}
