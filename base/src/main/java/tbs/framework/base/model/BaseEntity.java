package tbs.framework.base.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = -6140733829665609797L;

    private T id;
}
