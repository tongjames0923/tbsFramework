package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.async.task.consumers.IgnoreBroker;
import tbs.framework.utils.SingletonHolder;
import tbs.framework.utils.ThreadUtil;

/**
 * @author Abstergo
 */
@Data
@ConfigurationProperties(prefix = "tbs.framework.async.task")
public class AsyncTaskProperty {
    public static AsyncTaskProperty getInstance() {
        return SingletonHolder.getInstance(AsyncTaskProperty.class);
    }

    /**
     * 任务回执处理器
     */
    private Class<? extends ThreadUtil.IReceiptBroker> receiptBroker = IgnoreBroker.class;

    /**
     * 任务结果数据的留存时间(单位秒)
     */
    private long receiptTimeout = 30L;

}
