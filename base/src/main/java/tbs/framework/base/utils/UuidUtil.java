package tbs.framework.base.utils;

import cn.hutool.extra.spring.SpringUtil;

public abstract class UuidUtil {

    private static UuidUtil uuidUtils;

    /**
     * 获取uuidUtil实例
     *
     * @return
     */
    public static UuidUtil getInstance() {
        if (null == UuidUtil.uuidUtils) {
            UuidUtil.uuidUtils = SpringUtil.getBean(UuidUtil.class);
        }
        return UuidUtil.uuidUtils;
    }

    /**
     * 产生uuid
     *
     * @return
     */
    public abstract String uuid();

    /**
     * 获取uuid
     *
     * @return 产生的结果
     */
    public static String getUuid() {
        return UuidUtil.getInstance().uuid();
    }

}
