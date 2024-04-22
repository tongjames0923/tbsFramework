package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.multilingaul.ILocal;
import tbs.framework.base.multilingaul.aspects.MultilingualAspect;
import tbs.framework.base.multilingaul.impls.LocalStringTranslateImpl;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;

public class MultilingualConfig {
    private static ILogger log;

    public MultilingualConfig(LogUtil logUtil) {
        if (log == null) {
            log = logUtil.getLogger(MultilingualConfig.class.getName());
        }
    }

    @Bean
    public ILocal defaultLocal(LogUtil logUtil, MultilingualUtil multilingualUtil) {
        return new LocalStringTranslateImpl(logUtil, multilingualUtil);
    }

    @Bean
    public MultilingualAspect aspect() {
        return new MultilingualAspect();
    }

    @Bean
    public MultilingualUtil multilingualUtil(LogUtil logUtil) {
        return new MultilingualUtil(logUtil);
    }
}
