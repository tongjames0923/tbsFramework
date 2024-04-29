package tbs.framework.sql.config;

import org.springframework.context.annotation.Bean;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.sql.interfaces.impls.SimpleValueMapper;
import tbs.framework.sql.utils.BatchUtil;
import tbs.framework.sql.utils.QueryUtil;

public class SqlConfig {

    @Bean
    public BatchUtil batchUtil(LogUtil logUtil) {
        return new BatchUtil(logUtil);
    }

    @Bean
    public QueryUtil sqlUtil(LogUtil logUtil) {
        return new QueryUtil(logUtil);
    }

    @Bean
    public SimpleValueMapper simpleValueMapper() {
        return new SimpleValueMapper();
    }
}
