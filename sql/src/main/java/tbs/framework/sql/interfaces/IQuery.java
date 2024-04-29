package tbs.framework.sql.interfaces;

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
    default String pageQuerySql(IPage page) {
        return String.format(" limit %d,%d", page.from(), page.number());
    }
}
