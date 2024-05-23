package tbs.framework.sql.interfaces;

import tbs.framework.sql.model.SqlRuntimeStatus;

/**
 * mybatis sql 运行时日志记录器
 *
 * @author Abstergo
 */
public interface ISqlLogger {
    /**
     * Log.
     *
     * @param sqlRuntimeStatus the sql runtime status
     */
    void log(SqlRuntimeStatus sqlRuntimeStatus);
    
}
