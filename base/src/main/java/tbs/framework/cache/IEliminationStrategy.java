package tbs.framework.cache;

public interface IEliminationStrategy {
    void eliminate(String finalKey, ICacheService cacheService, String[] strArgs, int[] intArgs);
}
