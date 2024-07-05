package tbs.framework.cache.impls

import cn.hutool.core.util.StrUtil
import tbs.framework.base.model.AsyncReceipt
import tbs.framework.base.properties.AsyncTaskProperty
import tbs.framework.cache.AbstractTimeBaseCacheManager
import tbs.framework.utils.ThreadUtil.IReceiptBroker
import java.time.Duration
import javax.annotation.Resource

class CachedAsyncReceiptBroker : IReceiptBroker {

    @Resource
    private var cacheManager: AbstractTimeBaseCacheManager? = null


    override fun submitReceipt(receipt: AsyncReceipt?) {
        if (receipt == null) {
            return
        }
        if (StrUtil.isEmpty(receipt.id)) {
            return
        }
        cacheManager!!.put(receipt.id, receipt.returnValue);
        cacheManager!!.expire(receipt.id, Duration.ofSeconds(AsyncTaskProperty.getInstance().receiptTimeout))
    }

    override fun acknowledgeReceipt(receiptId: String?) {
        if (StrUtil.isEmpty(receiptId)) {
            return
        }
        cacheManager!!.remove(receiptId)
    }
}