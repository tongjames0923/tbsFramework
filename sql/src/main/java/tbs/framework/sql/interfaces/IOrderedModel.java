package tbs.framework.sql.interfaces;

import tbs.framework.sql.enums.QueryOrderEnum;

/**
 * @author abstergo
 */
public interface IOrderedModel {

    public QueryOrderEnum getOrdered();
}
