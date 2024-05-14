package tbs.framework.sql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import tbs.framework.base.intefaces.IChainProvider;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.interfaces.impls.NoSqlLogger;
import tbs.framework.sql.interfaces.impls.provider.BuiltInValueConvertChainProvider;
import tbs.framework.sql.utils.QueryUtil;
import tbs.framework.sql.utils.TransactionUtil;

public class SqlConfig {

    @Bean
    public QueryUtil sqlUtil() {
        return new QueryUtil();
    }

    @Bean
    public IChainProvider defaultvalueMappingProvider() {
        return new BuiltInValueConvertChainProvider();
    }

    @Bean
    public TransactionUtil transactionUtil(PlatformTransactionManager transactionManager) {
        return new TransactionUtil(transactionManager);
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.sql.enable-log-interceptor", havingValue = "true")
    SqlLoggingInterceptor sqlLoggingInterceptor() {
        return new SqlLoggingInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(ISqlLogger.class)
    @ConditionalOnBean(SqlLoggingInterceptor.class)
    public ISqlLogger noSqlLogger() {
        return new NoSqlLogger();
    }
}
