package tbs.framework.cache.strategy;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.managers.AbstractCacheManager;

import java.util.Set;

public abstract class AbstractCacheEliminationStrategy {

    public static enum CacheJudgeEnum {
        /**
         * 拒绝清除缓存
         */
        KeepAlive,
        /**
         * 缓存清除成功
         */
        Killed,

        /**
         * 缓存清除成功但是没有清除任何缓存
         */
        NoKillings,
        /**
         * 缓存清除失败
         */
        FailedToKill;
    }

    /**
     * 缓存清除判别器，指导缓存清除器操作
     *
     * @param <H>
     * @param <S>
     */

    public static interface ICacheEliminationJudge {
        /**
         * 判断是否执行清除
         */
        boolean isEliminated(AbstractCacheManager cacheManager);

        /**
         * 待清除的集合
         */
        @NotNull
        Set<String> killList();

        /**
         * 参数
         *
         * @param k 根据标识获取不同参数
         */
        <T> T paramter(int k);
    }

    /**
     * 缓存清除器
     *
     * @param <H>
     * @param <S>
     */
    public static interface ICacheEliminationBroker {

        /**
         * 清除缓存
         *
         * @param cacheManager 目标缓存管理器
         * @param judge        缓存判别器
         * @param true清除成功     ，反之失败
         */
        boolean eliminated(AbstractCacheManager cacheManager, @NotNull ICacheEliminationJudge judge);
    }

    /**
     * 获取缓存清除器
     */
    @NotNull
    protected abstract ICacheEliminationBroker getEliminationBroker();

    /**
     * 判别并执行缓存清除
     */
    public CacheJudgeEnum judgeAndClean(
        AbstractCacheManager cacheManager, @NotNull ICacheEliminationJudge judge) {
        if (!judge.isEliminated(cacheManager)) {
            return CacheJudgeEnum.KeepAlive;
        }
        if (judge.killList().isEmpty()) {
            return CacheJudgeEnum.NoKillings;
        }
        ICacheEliminationBroker broker = getEliminationBroker();
        if (broker == null) {
            throw new UnsupportedOperationException("The kill cache broker is null");
        }
        return broker.eliminated(cacheManager, judge) ? CacheJudgeEnum.Killed : CacheJudgeEnum.FailedToKill;
    }

}
