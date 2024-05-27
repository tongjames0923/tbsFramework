package tbs.framework.base.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 含有Serializable和id的基础实体类
 * @author Abstergo
 */
@Data
public class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = -6140733829665609797L;

    /**
     * 主键id
     */
    private T id;
}
