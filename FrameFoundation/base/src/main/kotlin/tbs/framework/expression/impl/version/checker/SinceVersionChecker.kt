package tbs.framework.expression.impl.version.checker

import tbs.framework.expression.IVersion
import tbs.framework.expression.IVersionChecker

/**
 * SinceVersionChecker实现了IVersionChecker接口，用于检查表达式代码的版本。
 * 这个版本检查器会检查主要版本号、次要版本号和修复版本号。无视修复版本号
 * 如果表达式代码的版本大于或等于指定的版本，那么检查结果为true，否则为false。
 *
 */
class SinceVersionChecker : IVersionChecker {

    private val mainVersion: Int
    private val subVersion: Int


    constructor(mainVersion: Int, subVersion: Int) {
        this.mainVersion = mainVersion
        this.subVersion = subVersion
    }

    override fun checkVersion(version: IVersion?): Boolean {
        if (version == null) {
            return false
        }
        if (version.mainVersion > mainVersion) {
            return true
        }
        if (version.mainVersion == mainVersion) {
            if (version.subVersion > subVersion) {
                return true
            }
        }
        return false
    }
}
