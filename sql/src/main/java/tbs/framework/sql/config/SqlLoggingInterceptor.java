package tbs.framework.sql.config;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.annotation.Lazy;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.model.SqlRuntimeStatus;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "query",
    args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SqlLoggingInterceptor implements Interceptor {

    @AutoLogger
    ILogger logger;

    @Resource
    @Lazy
    private Map<String, ISqlLogger> sqlLoggers;

    public SqlLoggingInterceptor() {
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Date date = new Date();
        Object result = invocation.proceed();
        Date date1 = new Date();
        long costTime = date1.getTime() - date.getTime();

        try {
            MappedStatement mappedStatement = (MappedStatement)invocation.getArgs()[0];
            String sql = mappedStatement.getBoundSql(invocation.getArgs()[1]).getSql();
            SqlCommandType commandType = mappedStatement.getSqlCommandType();

            for (ISqlLogger sqlLogger : sqlLoggers.values()) {
                if (null == sqlLogger) {
                    continue;
                }
                sqlLogger.log(new SqlRuntimeStatus(commandType, sql, costTime, invocation.getArgs()[1], result));
            }
        } catch (Exception e) {
            logger.error(e, e.getMessage());
        }


        return result;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
