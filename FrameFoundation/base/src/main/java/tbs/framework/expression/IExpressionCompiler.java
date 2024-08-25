package tbs.framework.expression;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * IExpressionCompiler接口定义了一个表达式编译器，用于编译表达式代码。
 *
 * @author Abstergo
 */
public interface IExpressionCompiler {
    /**
     * 获取编译器单元。
     *
     * @param code 表达式代码
     * @return 编译器单元
     */
    ICompilerUnit getCompilerUnit(InputStream code);

    /**
     * 获取编译器版本。
     *
     * @return 编译器版本
     */
    IVersion getVersion();

    /**
     * 获取版本检查器。
     *
     * @return 版本检查器
     */
    IVersionChecker getVersionChecker();
}
