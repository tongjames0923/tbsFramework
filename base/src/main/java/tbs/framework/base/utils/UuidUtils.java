package tbs.framework.base.utils;


import cn.hutool.core.lang.UUID;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UuidUtils {
    private static final Queue<String> uuids = new ConcurrentLinkedQueue<>();


    public static String getUuid() {
        if (uuids.isEmpty()) {
            for (int i = 0; i < 100; i++) {
                uuids.add(UUID.fastUUID().toString());
            }
        }
        return uuids.poll();
    }

}
