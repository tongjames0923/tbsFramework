package tbs.framework.sql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.SqlCommandType;

import java.io.Serializable;

/**
 * @author abstergo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlRuntimeStatus implements Serializable {

    private static final long serialVersionUID = -6384000507868094188L;
    private SqlCommandType commandType;
    private String prepareSql;
    private long executeTime;
    private Object parameterObject;
    private Object resultObject;
}
