package tbs.framework.sql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import tbs.framework.log.ILogger;
import tbs.framework.utils.LogUtil;
import tbs.framework.base.intefaces.IChainProvider;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.interfaces.impls.NoSqlLogger;
import tbs.framework.sql.interfaces.impls.provider.BuiltInValueConvertChainProvider;
import tbs.framework.sql.utils.QueryUtil;
import tbs.framework.sql.utils.TransactionUtil;

public class SqlConfig {


    ILogger logger;

    public SqlConfig(LogUtil logUtil) {
        logger = logUtil.getLogger(SqlConfig.class.getName());
    }

    @Bean
    public QueryUtil sqlUtil(final LogUtil logUtil) {
        return new QueryUtil(logUtil);
    }

    @Bean
    public IChainProvider defaultvalueMappingProvider() {
        return new BuiltInValueConvertChainProvider();
    }

    @Bean
    public TransactionUtil transactionUtil(PlatformTransactionManager transactionManager, LogUtil l) {
        return new TransactionUtil(transactionManager, l);
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.sql.enable-log-interceptor", havingValue = "true")
    SqlLoggingInterceptor sqlLoggingInterceptor(LogUtil logUtil) {
        return new SqlLoggingInterceptor(logUtil);
    }

    @Bean
    @ConditionalOnMissingBean(ISqlLogger.class)
    @ConditionalOnBean(SqlLoggingInterceptor.class)
    public ISqlLogger noSqlLogger() {
        return new NoSqlLogger();
    }
}
