package tbs.framework.sql.interfaces.impls;

import com.alibaba.fastjson2.JSON;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.model.SqlRuntimeStatus;


public class SimpleJsonLogger implements ISqlLogger {
    @AutoLogger
    ILogger logger;

    public SimpleJsonLogger() {
    }

    @Override
    public void log(SqlRuntimeStatus sqlRuntimeStatus) {
        logger.info(JSON.toJSONString(sqlRuntimeStatus));
    }
}
