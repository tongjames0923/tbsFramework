package tbs.framework.sql.interfaces.impls;

import com.alibaba.fastjson2.JSON;
import tbs.framework.log.ILogger;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.model.SqlRuntimeStatus;

/**
 * @author abstergo
 */
public class SimpleJsonLogger implements ISqlLogger {

    public SimpleJsonLogger(ILogger logger) {
        this.logger = logger;
    }

    private ILogger logger;
    @Override
    public void log(SqlRuntimeStatus sqlRuntimeStatus) {
        if (logger == null) {
            return;
        }
        logger.debug("prepareSql:{} cost:{}ms Params:{}", sqlRuntimeStatus.getPrepareSql(),
            sqlRuntimeStatus.getExecuteTime(), JSON.toJSONString(sqlRuntimeStatus.getParameterObject()));
    }
}
