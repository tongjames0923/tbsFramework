package tbs.framework.expression;

/**
 * IVersion接口定义了一个版本信息，用于表示表达式代码的版本。
 *
 * @author Abstergo
 * @version 1.0
 * @since 2022-01-01
 */
public interface IVersion {
    /**
     * 获取主要版本号。
     *
     * @return 主要版本号
     */
    int getMainVersion();

    /**
     * 获取次要版本号。
     *
     * @return 次要版本号
     */
    int getSubVersion();

    /**
     * 获取修复版本号。
     *
     * @return 修复版本号
     */
    Integer getFixVersion();

    /**
     * 获取版本名称。
     *
     * @return 版本名称
     */
    String getVersionName();

    /**
     * 获取版本字符串。
     *
     * @return 版本字符串
     */
    default String getVersionString() {
        return String.format("%s.%d.%d-%s", getVersionName(), getMainVersion(), getSubVersion(),
            getFixVersion() == null ? "" : String.valueOf(getFixVersion()));
    }
}
