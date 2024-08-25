package tbs.framework.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ICompilerUnit接口定义了一个编译器单元，用于执行编译任务。
 *
 * @author YourName
 * @version 1.0
 * @since 2022-01-01
 */
public interface ICompilerUnit {

    /**
     * 执行编译执行结果。
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class ExecuteResult {
        /**
         * 执行结果
         */
        private Object result;
    }

    /**
     * 执行编译器单元。
     *
     * @param context 表达式上下文
     * @param args
     * @return 执行结果
     * @throws Exception 编译异常
     */
    ExecuteResult exeCompilerUnit(IExpressionContext context, Object... args) throws Exception;

    /**
     * 获取表达式编译器。
     *
     * @return 表达式编译器
     */
    IExpressionCompiler compiler();
}
