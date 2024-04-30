package tbs.framework.sql.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.interfaces.impls.NoSqlLogger;
import tbs.framework.sql.interfaces.impls.SimpleValueMapper;
import tbs.framework.sql.properties.SqlProperty;
import tbs.framework.sql.utils.BatchUtil;
import tbs.framework.sql.utils.QueryUtil;
import tbs.framework.sql.utils.TransactionUtil;

import javax.annotation.Resource;

public class SqlConfig {

    @Resource
    SqlProperty sqlProperty;

    ILogger logger;

    public SqlConfig(LogUtil logUtil) {
        logger = logUtil.getLogger(SqlConfig.class.getName());
    }


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

    @Bean
    @Order
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

//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, SqlLoggingInterceptor sqlLoggingInterceptor) {
//        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
//        sqlSessionFactory.setPlugins(new Interceptor[] {sqlLoggingInterceptor});
//        sqlSessionFactory.setDataSource(dataSource);
//        // 配置数据源等其他信息
//        return sqlSessionFactory;
//    }
}
