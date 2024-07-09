package tbs.framework.cache.supports;

import java.time.Duration;

/**
 * @author Abstergo
 */
public interface ITimeBaseCacheSupport {
    void expire(String key, Duration time);

    Duration remaining(String key);
}
