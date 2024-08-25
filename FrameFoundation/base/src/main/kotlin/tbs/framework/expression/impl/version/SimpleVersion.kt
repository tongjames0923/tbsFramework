package tbs.framework.expression.impl.version

import tbs.framework.expression.IVersion

class SimpleVersion : IVersion {

    private val mainVersion: Int;
    private val subVersion: Int;
    private val fixVersion: Int?;

    constructor(mainVersion: Int, subVersion: Int, fixVersion: Int?) {
        this.mainVersion = mainVersion;
        this.subVersion = subVersion;
        this.fixVersion = fixVersion;
    }

    override fun getMainVersion(): Int {
        return mainVersion
    }

    override fun getSubVersion(): Int {
        return subVersion
    }

    override fun getFixVersion(): Int? {
        return fixVersion
    }

    override fun getVersionName(): String {
        return ""
    }
}