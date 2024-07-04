package tbs.framework.async.task.consumers

import tbs.framework.base.model.AsyncReceipt
import tbs.framework.utils.ThreadUtil.IReceiptConsumer

class IgnoreConsumer : IReceiptConsumer {
    override fun consumeReceipt(receipt: AsyncReceipt?) {
    }
}