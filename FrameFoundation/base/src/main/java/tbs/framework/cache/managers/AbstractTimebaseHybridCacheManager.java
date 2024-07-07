package tbs.framework.cache.managers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.constants.FeatureSupportCode;
import tbs.framework.cache.hooks.IHybridCacheServiceHook;
import tbs.framework.cache.supports.ICacheServiceHybridSupport;
import tbs.framework.utils.BeanUtil;

import java.util.ArrayList;
import java.util.function.BiPredicate;

/**
 * @author abstergo
 */
public abstract class AbstractTimebaseHybridCacheManager extends AbstractTimeBaseCacheManager
    implements ICacheServiceHybridSupport, InitializingBean {
    private int m_serviceIndex = -1;
    private ArrayList<ICacheService> cacheServiceArrayList = new ArrayList<>(8);

    @Override
    public int serviceIndex() {
        return m_serviceIndex;
    }

    @Override
    public void setService(int index) {
        if (index >= cacheServiceArrayList.size()) {
            throw new ArrayIndexOutOfBoundsException("index is too large");
        }
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onSwitch(this, cacheServiceArrayList.get(m_serviceIndex), cacheServiceArrayList.get(index));
        }, IHybridCacheServiceHook.HOOK_OPERATE_SWITCH);

        this.m_serviceIndex = index;
    }

    @Override
    public int selectService(BiPredicate<ICacheService, Integer> condition) {
        int i = -1;
        for (ICacheService cacheService : cacheServiceArrayList) {
            if (condition.test(cacheService, ++i)) {
                break;
            }

        }
        return i;
    }

    @Override
    public void addService(@NotNull ICacheService service) {
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onNewCacheServiceAdd(this, service, cacheServiceArrayList.size());
        }, IHybridCacheServiceHook.HOOK_OPERATE_ADD_SERVICE);
        cacheServiceArrayList.add(service);
    }

    @Override
    public void removeService(int index) {
        foreachHook((h) -> {
            IHybridCacheServiceHook hook = BeanUtil.getAs(h);
            hook.onServiceRemove(this, cacheServiceArrayList.get(index), cacheServiceArrayList.size());
        }, IHybridCacheServiceHook.HOOK_OPERATE_REMOVE_SERVICE);
        cacheServiceArrayList.remove(index);

    }

    @Override
    public int serviceCount() {
        return cacheServiceArrayList.size();
    }

    @Override
    public ICacheService getCacheService() {
        return cacheServiceArrayList.get(serviceIndex());
    }

    @Override
    public boolean featureSupport(int code) {
        return super.featureSupport(code) || code == FeatureSupportCode.HYBRID_CACHE_SERVICE;
    }
}
