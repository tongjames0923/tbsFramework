package tbs.framework.sql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.sql.interfaces.impls.SimpleValueMapper;
import tbs.framework.sql.utils.BatchUtil;
import tbs.framework.sql.utils.QueryUtil;
import tbs.framework.sql.utils.TransactionUtil;

public class SqlConfig {

    @Bean
    public BatchUtil batchUtil(final LogUtil logUtil) {
        return new BatchUtil(logUtil);
    }

    @Bean
    public QueryUtil sqlUtil(final LogUtil logUtil) {
        return new QueryUtil(logUtil);
    }

    @Bean
    public SimpleValueMapper simpleValueMapper() {
        return new SimpleValueMapper();
    }

    @Bean
    public TransactionUtil transactionUtil(PlatformTransactionManager transactionManager, LogUtil l) {
        return new TransactionUtil(transactionManager, l);
    }
}
