package tbs.framework.expression;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * IExpressionContext接口定义了一个表达式上下文，用于存储和管理变量。
 *
 * @author Abstergo
 */
public interface IExpressionContext {
    /**
     * 获取变量映射。
     *
     * @return 变量映射
     */
    Map<String, Object> getVariablesMap();

    /**
     * 设置变量。
     *
     * @param name  变量名
     * @param value 变量值
     * @return 当前表达式上下文
     */
    IExpressionContext setVariable(@NotNull String name, Object value);

    /**
     * 获取变量。
     *
     * @param name 变量名
     * @return 变量值
     */
    Object getVariable(@NotNull String name);

}
