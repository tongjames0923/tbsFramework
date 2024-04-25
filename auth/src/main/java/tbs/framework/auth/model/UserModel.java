package tbs.framework.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户数据
 * @author abstergo
 */
@Data
public class UserModel implements Serializable {
    private static final long serialVersionUID = 87629562341030460L;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户权限集
     */
    private Set<String> userRole;
}
