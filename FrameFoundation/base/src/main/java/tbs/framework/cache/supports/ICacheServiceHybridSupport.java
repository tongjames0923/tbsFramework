package tbs.framework.cache.supports;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;

import java.util.function.BiPredicate;

/**
 * The interface Cache service hybrid support.
 */
public interface ICacheServiceHybridSupport {

    public static final int WRITE_PUT = 1, WRITE_REMOVE = 2, WRITE_CLEAR = 3;
    public static final int READ_GET = 1, READ_TEST = 2;

    /**
     * Service index int.
     *
     * @return the int
     */
    int serviceIndex();

    ICacheService getServiceByIndex(int i);

    /**
     * Sets service.
     *
     * @param index the index
     */
    void setService(int index);

    /**
     * Select service int.
     *
     * @param condition the condition
     * @return the int
     */
    int selectService(BiPredicate<ICacheService, Integer> condition);

    /**
     * Add service.
     *
     * @param service the service
     */
    void addService(@NotNull ICacheService service);

    /**
     * Remove service.
     *
     * @param index the index
     */
    void removeService(int index);

    /**
     * Service count int.
     *
     * @return the int
     */
    int serviceCount();

    void collaborativeWriting(String key, Object value, boolean overwrite, int flag);

    void collaborativeReading(String key, int flag);

}
