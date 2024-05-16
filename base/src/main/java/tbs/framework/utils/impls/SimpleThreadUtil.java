package tbs.framework.utils.impls;

import org.springframework.beans.factory.DisposableBean;
import tbs.framework.utils.ThreadUtil;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Abstergo
 */
public class SimpleThreadUtil extends ThreadUtil implements DisposableBean {

    @Resource
    private ExecutorService executorService;

    @Override
    protected ExecutorService getExecutorService() {
        if (executorService.isShutdown()) {
            throw new RuntimeException("executorService has been shutdown");
        }
        return executorService;
    }

    @Override
    public void destroy() throws Exception {
        if (executorService.isShutdown()) {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
