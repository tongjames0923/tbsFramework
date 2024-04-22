package tbs.framework.base.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.log.ILogger;
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
    public MultilingualUtil multilingualUtil(LogUtil logUtil) {
        return new MultilingualUtil(logUtil);
    }
}
