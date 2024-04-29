package tbs.framework.sql.model;

import tbs.framework.sql.interfaces.IPage;

import java.io.Serializable;

public class Page implements IPage, Serializable {

    private static final long serialVersionUID = 7922366116007242398L;
    private final long page;
    private final long perPage;

    public Page(final long page, final long perPage) {
        if (1 > page) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        if (1 > perPage) {
            throw new IllegalArgumentException("PerPage must be greater than 0");
        }
        this.page = page;
        this.perPage = perPage;
    }

    @Override
    public long from() {
        return (this.page - 1) * this.perPage;
    }

    @Override
    public long number() {
        return this.perPage;
    }
}
