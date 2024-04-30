package tbs.framework.sql.config;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.sql.interfaces.impls.SimpleValueMapper;
import tbs.framework.sql.utils.BatchUtil;
import tbs.framework.sql.utils.QueryUtil;
import tbs.framework.sql.utils.TransactionUtil;

import javax.sql.DataSource;

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

    @Bean
    SqlLoggingInterceptor sqlLoggingInterceptor(LogUtil logUtil) {
        return new SqlLoggingInterceptor(logUtil);
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
