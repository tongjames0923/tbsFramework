package tbs.framework.base.utils;


import cn.hutool.core.lang.UUID;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum UuidUtils {
    ;
    private static final Queue<String> uuids = new ConcurrentLinkedQueue<>();
    public static String getUuid() {
        if (UuidUtils.uuids.isEmpty()) {
            for (int i = 0; 100 > i; i++) {
                UuidUtils.uuids.add(UUID.fastUUID().toString());
            }
        }
        return UuidUtils.uuids.poll();
    }

}
