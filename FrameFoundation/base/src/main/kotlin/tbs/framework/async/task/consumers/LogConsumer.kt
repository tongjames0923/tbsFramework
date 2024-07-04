package tbs.framework.async.task.consumers

import tbs.framework.base.model.AsyncReceipt
import tbs.framework.log.ILogger
import tbs.framework.log.annotations.AutoLogger
import tbs.framework.utils.ThreadUtil.IReceiptConsumer

class LogConsumer : IReceiptConsumer {

    @AutoLogger
    var logger: ILogger? = null;

    override fun consumeReceipt(receipt: AsyncReceipt?) {
        if (receipt != null) {
            logger?.info(receipt.toString());
        }
    }
}