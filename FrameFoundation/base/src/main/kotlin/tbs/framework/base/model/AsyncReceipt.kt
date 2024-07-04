package tbs.framework.base.model

import java.io.Serializable
import java.lang.reflect.Method
import java.time.LocalDateTime

data class AsyncReceipt(

    val id: String,
    val invokeTime: LocalDateTime,
    val finishTime: LocalDateTime,
    val parameters: List<Any?>,
    val returnValue: Any?,
    val method: Method
) : Serializable {

    companion object {
        private val serialVersionUID = -6140733829665609797L
    }


    override fun toString(): String {
        val sb = StringBuilder("[")
        for (param in parameters) {
            sb.append(param.toString()).append(",")
        }
        sb.append("]")
        return "AsyncReceipt(id='${id.toString()}', invokeTime=${invokeTime.toString()}, finishTime=${finishTime.toString()}, parameters=${sb.toString()}, returnValue=$returnValue, method=$method)"
    }
}
