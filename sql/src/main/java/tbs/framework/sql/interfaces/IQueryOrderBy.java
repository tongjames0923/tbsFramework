package tbs.framework.sql.interfaces;

import tbs.framework.sql.enums.QueryOrderEnum;

import java.lang.reflect.Field;

public interface IQueryOrderBy {
    QueryOrderEnum orderBy(IQuery target, Field field, String mappedName);
}
