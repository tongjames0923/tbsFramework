package tbs.framework.base.model

import lombok.Data
import java.io.Serializable

/**
 * 含有Serializable和id的基础实体类
 * @author Abstergo
 */
@Data
class BaseEntity<T> : Serializable {
    /**
     * 主键id
     */
    private val id: T? = null

    companion object {
        private val serialVersionUID = -6140733829665609797L
    }
}
