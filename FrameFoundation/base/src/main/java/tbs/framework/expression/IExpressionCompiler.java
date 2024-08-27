package tbs.framework.expression;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

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
    ICompilerUnit getCompilerUnit(@NotNull InputStream code);

    /**
     * 获取编译器单元。
     * @param code
     * @return
     */
    default ICompilerUnit getCompilerUnit(@NotNull String code) {
        return getCompilerUnit(
            new BufferedInputStream(new ByteArrayInputStream(code.getBytes(Charset.defaultCharset()))));
    }

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
