package tbs.framework.base.utils;

/**
 * 当Application 启动后运行
 *
 * @author abstergo
 */
public interface IStartup {
    /**
     * 运行方法
     *
     * @throws RuntimeException
     */
    void startUp() throws RuntimeException;
}
