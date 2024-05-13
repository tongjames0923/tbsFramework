package tbs.framework.base.utils;

import org.springframework.core.Ordered;

/**
 * 当Application 启动后运行
 *
 * @author abstergo
 */
public interface IStartup extends Ordered {
    /**
     * 运行方法
     *
     * @throws RuntimeException
     */
    void startUp() throws RuntimeException;

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
