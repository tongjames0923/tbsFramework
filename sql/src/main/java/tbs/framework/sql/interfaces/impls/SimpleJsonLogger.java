package tbs.framework.sql.interfaces.impls;

import com.alibaba.fastjson2.JSON;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.model.SqlRuntimeStatus;


public class SimpleJsonLogger implements ISqlLogger {
    ILogger logger;

    public SimpleJsonLogger(LogUtil logUtil) {
        logger = logUtil.getLogger(this.getClass().getName());
    }

    @Override
    public void log(SqlRuntimeStatus sqlRuntimeStatus) {
        logger.info(JSON.toJSONString(sqlRuntimeStatus));
    }
}
