package tbs.framework.sql.interfaces;

public interface IPage {
    /**
     * 偏移量
     *
     * @return
     */
    long from();

    /**
     * 数据量
     *
     * @return
     */
    long number();
}
