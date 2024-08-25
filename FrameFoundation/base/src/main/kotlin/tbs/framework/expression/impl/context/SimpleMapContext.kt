package tbs.framework.expression.impl.context

import tbs.framework.expression.IExpressionContext

class SimpleMapContext : IExpressionContext {

    val variablesMap = HashMap<String, Any?>()

    override fun getVariablesMap(): MutableMap<String, Any?> {
        return variablesMap
    }

    override fun setVariable(name: String, value: Any?): IExpressionContext {
        variablesMap[name] = value;
        return this
    }

    override fun getVariable(name: String): Any? {
        return variablesMap[name]
    }
}