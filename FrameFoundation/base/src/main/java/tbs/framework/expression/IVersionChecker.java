package tbs.framework.expression;

import java.io.InputStream;

/**
 * IVersionChecker接口定义了一个版本检查器，用于检查表达式代码的版本。
 *
 * @author Abstergo
 */
public interface IVersionChecker {
    /**
     * 检查表达式代码的版本。
     *
     * @param code 表达式代码
     * @return 是否与编译器版本兼容
     */
    boolean checkVersion(IVersion version);
}
