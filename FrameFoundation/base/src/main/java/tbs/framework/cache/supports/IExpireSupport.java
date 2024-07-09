package tbs.framework.cache.supports;

import java.time.Duration;

/**
 * 缓存管理器超时功能支持
 *
 * @author Abstergo
 */
public interface IExpireSupport {
    /**
     * 针对指定的key的缓存做超时移除处理
     *
     * @param key  缓存key
     * @param time 超时时间
     */
    void expire(String key, Duration time);

    /**
     * @param key 指定的key
     * @return 剩余的时间
     */
    Duration remaining(String key);
}
