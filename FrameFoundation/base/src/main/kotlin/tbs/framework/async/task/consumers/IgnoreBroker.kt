package tbs.framework.async.task.consumers

import tbs.framework.base.model.AsyncReceipt
import tbs.framework.utils.ThreadUtil.IReceiptBroker

class IgnoreBroker : IReceiptBroker {
    override fun submitReceipt(receipt: AsyncReceipt?) {
    }

    override fun acknowledgeReceipt(receiptId: String?) {
        TODO("Not yet implemented")
    }
}