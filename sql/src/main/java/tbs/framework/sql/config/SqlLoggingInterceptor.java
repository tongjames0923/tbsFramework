package tbs.framework.sql.config;

import com.alibaba.fastjson2.JSON;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;

import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
    @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SqlLoggingInterceptor implements Interceptor {

    ILogger logger;

    public SqlLoggingInterceptor(LogUtil logUtil) {
        this.logger = logUtil.getLogger(SqlLoggingInterceptor.class.getName());
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Date date = new Date();
        Object result = invocation.proceed();
        Date date1 = new Date();
        long costTime = date1.getTime() - date.getTime();
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        String sql = statementHandler.getBoundSql().getSql();
        logger.info(String.format("sql execute:[%s] cost: %d ms\n parameters:[%s]", sql, costTime,
            JSON.toJSONString(statementHandler.getParameterHandler().getParameterObject())));
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
