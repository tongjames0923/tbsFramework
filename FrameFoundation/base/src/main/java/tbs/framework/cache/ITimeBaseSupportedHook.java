package tbs.framework.cache;

import java.time.Duration;

/**
 * @author Abstergo
 */
public interface ITimeBaseSupportedHook extends ICacheServiceHook {
    void onSetDelay(String key, Duration delay, ICacheService service);

    void onTimeout(String key, ICacheService service);

    long remainingTime(String key, ICacheService service);
}
