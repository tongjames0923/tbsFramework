package tbs.framework.async.task.consumers

import tbs.framework.base.model.AsyncReceipt
import tbs.framework.log.ILogger
import tbs.framework.log.annotations.AutoLogger
import tbs.framework.utils.ThreadUtil.IReceiptBroker

class LogBroker : IReceiptBroker {

    @AutoLogger
    var logger: ILogger? = null;

    override fun submitReceipt(receipt: AsyncReceipt?) {
        if (receipt != null) {
            logger?.info(receipt.toString());
        }
    }

    override fun acknowledgeReceipt(receiptId: String?) {
        TODO("Not yet implemented")
    }
}