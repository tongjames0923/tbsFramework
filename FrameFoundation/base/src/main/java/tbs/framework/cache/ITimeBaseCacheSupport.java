package tbs.framework.cache;

import java.time.Duration;

/**
 * @author Abstergo
 */
public interface ITimeBaseCacheSupport {

    void expire(String key, Duration time);

    Duration remaining(String key);

    Object getAndRemove(String key, Duration delay);

}
