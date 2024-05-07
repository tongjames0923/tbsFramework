package tbs.framework.cache;

/**
 * 缓存设置处理器
 * @author Abstergo
 */
public interface ICacheBroker {
    /**
     * 设置缓存
     * @param key 键
     * @param cacheService 缓存服务
     * @param value 值
     * @param intArgs 运行时整数参数
     * @param strArgs 运行时字符串参数
     */
    void setCache(String key,ICacheService cacheService, Object value, int[] intArgs, String[] strArgs);
}
