package tbs.framework.sql.model;

import tbs.framework.sql.enums.QueryOrderEnum;
import tbs.framework.sql.interfaces.IOrderedModel;

/**
 * @author abstergo
 */
public class OrderedModel implements IOrderedModel {
    private QueryOrderEnum ordered;

    public OrderedModel(QueryOrderEnum ordered) {
        this.ordered = ordered;
    }

    public OrderedModel() {
    }

    public void setOrdered(QueryOrderEnum ordered) {
        this.ordered = ordered;
    }

    @Override
    public QueryOrderEnum getOrdered() {
        return ordered;
    }
}
