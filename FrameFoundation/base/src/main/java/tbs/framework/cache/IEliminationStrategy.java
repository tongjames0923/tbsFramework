package tbs.framework.cache;

/**
 * 缓存淘汰策略,用于CacheLoad和CacheUnLoad注解
 *
 * @author Abstergo
 */
public interface IEliminationStrategy {
    /**
     * 淘汰缓存
     *
     * @param finalKey     键
     * @param cacheService 缓存服务
     * @param strArgs      字符串参数
     * @param intArgs      数字参数
     */
    void eliminate(String finalKey, AbstractTimeBaseCacheManager cacheService, String[] strArgs, int[] intArgs);
}
