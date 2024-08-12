package tbs.framework.sql.interfaces.impls.orders;

import tbs.framework.sql.enums.QueryOrderEnum;
import tbs.framework.sql.interfaces.IOrderedModel;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.interfaces.IQueryOrderBy;

import java.lang.reflect.Field;

public class OrderedModelOrderImpl implements IQueryOrderBy {
    @Override
    public QueryOrderEnum orderBy(IQuery target, Field field, String mappedName) {
        if (IOrderedModel.class.isAssignableFrom(field.getType())) {
            try {
                IOrderedModel orderedModel = (IOrderedModel)field.get(target);
                return orderedModel.getOrdered();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("only support for IOrderedModel");
        }
    }
}
