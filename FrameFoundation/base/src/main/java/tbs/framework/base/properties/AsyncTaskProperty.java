package tbs.framework.base.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.async.task.consumers.IgnoreConsumer;
import tbs.framework.utils.ThreadUtil;

@Data
@ConfigurationProperties(prefix = "tbs.framework.async.task")
public class AsyncTaskProperty {
    private Class<? extends ThreadUtil.IReceiptConsumer> receiptConsumer = IgnoreConsumer.class;

}
