package tbs.framework.cache;

import java.util.Optional;

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
     * @param key 键
     * @param value 值
     * @param override 存在时是否覆盖
     */
    void put(String key, Object value, boolean override);

    /**
     * 获取值
     *
     * @param key      键
     * @param isRemove 是否移除
     * @param delay 移除延迟 单位秒
     * @return 获取的值
     */
    Optional get(String key, boolean isRemove,long delay);

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
     * 设置超时
     *
     * @param key 键
     * @param seconds 超时时间 单位秒
     */
    void expire(String key, long seconds);

    /**
     * 超时剩余 不存在返回Long.MIN_VALUE 单位秒
     *
     * @param key a {@link java.lang.String} object
     * @return a long
     */
    long remain(String key);
}
