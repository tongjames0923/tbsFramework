package tbs.framework.sql.interfaces;

/**
 * @author abstergo
 */
public interface IQuery {
    /**
     * 基础查询语句
     *
     * @return
     */
    String baseQuerySql();

    /**
     * 分页查询语句
     *
     * @param page
     * @return
     */
    default String pageQuerySql(final IPage page) {
        return String.format(" limit %d,%d", page.from(), page.number());
    }
}
