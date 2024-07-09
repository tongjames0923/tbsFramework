package tbs.framework.cache.supports;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * The interface Cache service hybrid support.
 */
public interface ICacheServiceHybridSupport {
    /**
     * Service index int.
     *
     * @return the int
     */
    int serviceIndex();

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

    /**
     * Operate cache service.
     *
     * @param index     the index
     * @param operation the operation
     */
    void operateCacheService(@NotNull int index, @NotNull Consumer<ICacheService> operation);
}
