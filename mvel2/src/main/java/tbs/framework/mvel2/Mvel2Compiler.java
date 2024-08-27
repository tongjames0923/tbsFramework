package tbs.framework.mvel2;

import org.mvel2.MVEL;
import tbs.framework.expression.ICompilerUnit;
import tbs.framework.expression.IExpressionCompiler;
import tbs.framework.expression.IVersion;
import tbs.framework.expression.IVersionChecker;
import tbs.framework.expression.impl.version.SimpleVersion;
import tbs.framework.expression.impl.version.checker.SinceVersionChecker;
import tbs.framework.utils.StrUtil;

import java.io.InputStream;

/**
 * @author Abstergo
 */
public class Mvel2Compiler implements IExpressionCompiler {

    @Override
    public ICompilerUnit getCompilerUnit(InputStream code) {
        return new MvelCompilerUnit(MVEL.compileExpression(StrUtil.readStreamAsString(code)), this);
    }

    @Override
    public IVersion getVersion() {
        return new SimpleVersion(2, 0, 0);
    }

    @Override
    public IVersionChecker getVersionChecker() {
        return new SinceVersionChecker(2, 0);
    }
}
