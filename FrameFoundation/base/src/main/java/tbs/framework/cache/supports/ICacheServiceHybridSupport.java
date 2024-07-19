package tbs.framework.cache.supports;

import org.jetbrains.annotations.NotNull;
import tbs.framework.cache.ICacheService;

import java.util.function.BiPredicate;

/**
 * The interface Cache service hybrid support.
 *
 * @author Abstergo
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


}
