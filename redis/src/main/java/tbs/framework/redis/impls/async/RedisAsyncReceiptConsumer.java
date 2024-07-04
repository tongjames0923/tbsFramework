package tbs.framework.redis.impls.async;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.RedisTemplate;
import tbs.framework.base.model.AsyncReceipt;
import tbs.framework.redis.properties.RedisAsyncTaskProperty;
import tbs.framework.utils.ThreadUtil;

import javax.annotation.Resource;

public class RedisAsyncReceiptConsumer implements ThreadUtil.IReceiptConsumer {

    @Resource
    RedisAsyncTaskProperty asyncTaskProperty;

    public final String redisKey(String id) {
        return asyncTaskProperty.getKeyPrefix() + id;
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void consumeReceipt(AsyncReceipt receipt) {
        if (receipt == null) {
            return;
        }
        if (StrUtil.isEmpty(receipt.getId())) {
            return;
        }
        redisTemplate.opsForValue()
            .set(redisKey(receipt.getId()), receipt.getReturnValue(), asyncTaskProperty.getTimeout());
    }
}
