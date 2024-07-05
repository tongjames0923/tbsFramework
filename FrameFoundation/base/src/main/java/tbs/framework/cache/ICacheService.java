package tbs.framework.cache;

/**
 * <p>ICacheService interface.</p>
 *
 * @author abstergo
 * @version $Id: $Id
 */
public interface ICacheService {

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
     * @param key      键
     * @param isRemove 是否移除
     * @return 获取的值
     */
    Object get(String key);

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

    long cacheSize();
}
