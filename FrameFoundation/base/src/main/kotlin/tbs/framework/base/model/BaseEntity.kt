package tbs.framework.base.model

import java.io.Serializable

/**
 * 含有Serializable和id的基础实体类
 * @author Abstergo
 */
open class BaseEntity<T>(
    /**
     * 主键id
     */
    var id: T? = null
) : Serializable {


    companion object {
        private val serialVersionUID = -6140733829665609797L
    }
}
