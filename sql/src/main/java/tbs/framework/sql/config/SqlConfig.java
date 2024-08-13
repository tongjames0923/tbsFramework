package tbs.framework.sql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import tbs.framework.base.interfaces.IChainProvider;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.sql.constants.OrderConstant;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.interfaces.extractors.ISqlExtractor;
import tbs.framework.sql.interfaces.extractors.impls.mysql.MysqlExatractor;
import tbs.framework.sql.interfaces.impls.NoSqlLogger;
import tbs.framework.sql.interfaces.impls.orders.AscStaticOrder;
import tbs.framework.sql.interfaces.impls.orders.DescStaticOrder;
import tbs.framework.sql.interfaces.impls.orders.OrderedModelOrderImpl;
import tbs.framework.sql.interfaces.impls.provider.BuiltInValueConvertChainProvider;
import tbs.framework.sql.utils.QueryUtil;
import tbs.framework.sql.utils.TransactionUtil;
import tbs.framework.utils.impls.Slf4JLoggerFactory;

/**
 * @author abstergo
 */
public class SqlConfig {

    public static final String SQL_LOG_FACTORY_BEAN_NAME = "sqlLogFactory";

    @Bean
    @ConditionalOnMissingBean(ISqlExtractor.class)
    ISqlExtractor mysqlWhereSql() {
        return new MysqlExatractor();
    }

    @Bean
    public QueryUtil sqlUtil(ISqlExtractor whereSqlExtractor) {
        return new QueryUtil(whereSqlExtractor);
    }

    @Bean
    public IChainProvider defaultvalueMappingProvider() {
        return new BuiltInValueConvertChainProvider();
    }

    @Bean
    public TransactionUtil transactionUtil(PlatformTransactionManager transactionManager) {
        return new TransactionUtil(transactionManager);
    }

    @Bean(SQL_LOG_FACTORY_BEAN_NAME)
    public LogFactory sqlLogFactory() {
        return new Slf4JLoggerFactory();
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.sql.enable-log-interceptor", havingValue = "true")
    SqlLoggingInterceptor sqlLoggingInterceptor() {
        return new SqlLoggingInterceptor();
    }

    @Bean
    @ConditionalOnProperty(name = "tbs.framework.sql.enable-auto-fill-value-interceptor", havingValue = "true")
    SqlAutoFillIntercepter autoFillIntercepter() {
        return new SqlAutoFillIntercepter();
    }

    @Bean
    @ConditionalOnMissingBean(ISqlLogger.class)
    @ConditionalOnBean(SqlLoggingInterceptor.class)
    public ISqlLogger noSqlLogger() {
        return new NoSqlLogger();
    }

    @Bean(OrderConstant.ASC)
    public AscStaticOrder ascOrder() {
        return new AscStaticOrder();
    }

    @Bean(OrderConstant.DESC)
    public DescStaticOrder descOrder() {
        return new DescStaticOrder();
    }

    @Bean(OrderConstant.ORDERED_MODEL)
    public OrderedModelOrderImpl orderedModelOrderImpl() {
        return new OrderedModelOrderImpl();
    }
}
