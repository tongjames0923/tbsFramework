package tbs.framework.sql.interfaces;

import tbs.framework.sql.model.SqlRuntimeStatus;

public interface ISqlLogger {
    void log(SqlRuntimeStatus sqlRuntimeStatus);
}
