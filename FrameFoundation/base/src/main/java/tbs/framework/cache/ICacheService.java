package tbs.framework.cache;

import tbs.framework.cache.constants.CacheServiceTypeCode;

/**
 * <p>ICacheService interface.</p>
 *
 * @author abstergo
 * @version $Id : $Id
 */
public interface ICacheService {

    /**
     * Service type int.
     *
     * @return the int
     */
    default int serviceType() {
        return CacheServiceTypeCode.LOCAL;
    }

    /**
     * 设置缓存
     *
     * @param key      键
     * @param value    值
     * @param override 存在时是否覆盖
     */
    void put(String key, Object value, boolean override);

    /**
     * 获取值
     *
     * @param key 键
     * @return 获取的值 object
     */
    Object get(String key);

    /**
     * Exists boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean exists(String key);

    /**
     * 移除值
     *
     * @param key 键
     */
    void remove(String key);

    /**
     * 清空值
     */
    void clear();

    /**
     * Cache size long.
     *
     * @return the long
     */
    long cacheSize();
}
