package tbs.framework.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import tbs.framework.cache.ICacheService;
import tbs.framework.cache.impls.services.ConcurrentMapCacheServiceImpl;

@Data
@ConfigurationProperties(prefix = "tbs.framework.cache")
public class CacheProperty {
    /**
     * 缓存服务是否接收空值
     */
    private boolean acceptNullValues = false;

    /**
     * 默认的主要缓存服务
     */
    private Class<? extends ICacheService> cacheServiceClass = ConcurrentMapCacheServiceImpl.class;
}
