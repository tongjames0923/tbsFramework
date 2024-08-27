package tbs.framework.base

import tbs.framework.base.interfaces.IMethodInterceptHandler
import java.lang.reflect.Method

class NotingMethodIntercept:IMethodInterceptHandler {
    override fun handleArgs(target: Any, method: Method, vararg args: Any): Array<Any> {
        TODO("Not yet implemented")
    }

    override fun handleException(e: Throwable, target: Any, method: Method, vararg args: Any) {
        TODO("Not yet implemented")
    }

    override fun handleReturn(
        target: Any,
        method: Method,
        result: Any?,
        vararg args: Any
    ): IMethodInterceptHandler.HandleReturnedResult {
        TODO("Not yet implemented")
    }

}