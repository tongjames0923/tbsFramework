package tbs.framework.utils;

/**
 * @author abstergo
 */
public abstract class UuidUtil {

    /**
     * 获取uuidUtil实例
     *
     * @return
     */
    public static UuidUtil getInstance() {
        return SingletonHolder.getInstance(UuidUtil.class);
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
