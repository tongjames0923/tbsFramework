package tbs.framework.sql.model;

import tbs.framework.sql.interfaces.IPage;

import java.io.Serializable;

public class Page implements IPage, Serializable {

    private static final long serialVersionUID = 7922366116007242398L;
    private long page;
    private long perPage;

    public Page(long page, long perPage) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        if (perPage < 1) {
            throw new IllegalArgumentException("PerPage must be greater than 0");
        }
        this.page = page;
        this.perPage = perPage;
    }

    @Override
    public long from() {
        return (page - 1) * perPage;
    }

    @Override
    public long number() {
        return perPage;
    }
}
