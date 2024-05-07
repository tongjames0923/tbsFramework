package tbs.framework.base.utils.impls;

import cn.hutool.core.lang.UUID;
import tbs.framework.base.utils.UuidUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleUuidUtil extends UuidUtil {
    private static final Queue<String> uuids = new ConcurrentLinkedQueue<>();

    @Override
    public String uuid() {
        if (SimpleUuidUtil.uuids.isEmpty()) {
            for (int i = 0; 100 > i; i++) {
                SimpleUuidUtil.uuids.add(UUID.fastUUID().toString());
            }
        }
        return SimpleUuidUtil.uuids.poll();
    }
}
