package tbs.framework.sql.interfaces.impls;

import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.model.SqlRuntimeStatus;

/**
 * 空SQL日志器
 * @author abstergo
 */
public class NoSqlLogger implements ISqlLogger {
    private boolean hasShow;

    @Override
    public void log(SqlRuntimeStatus sqlRuntimeStatus) {
        if (!hasShow) {
            hasShow = true;
            throw new UnsupportedOperationException("Not SQL logger implementation");
        }
    }
}
