package tbs.framework.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author abstergo
 */
@Data
public class UserModel implements Serializable {
    private static final long serialVersionUID = 87629562341030460L;
    private String userId;
    private Set<String> userRole;
}
