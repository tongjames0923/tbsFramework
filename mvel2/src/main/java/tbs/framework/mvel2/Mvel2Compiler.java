package tbs.framework.mvel2;

import org.mvel2.MVEL;
import tbs.framework.expression.ICompilerUnit;
import tbs.framework.expression.IExpressionCompiler;
import tbs.framework.expression.IVersion;
import tbs.framework.expression.IVersionChecker;
import tbs.framework.expression.impl.version.SimpleVersion;
import tbs.framework.expression.impl.version.checker.SinceVersionChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author Abstergo
 */
public class Mvel2Compiler implements IExpressionCompiler {

    @Override
    public ICompilerUnit getCompilerUnit(InputStream code) {
        String result =
            new BufferedReader(new InputStreamReader(code)).lines().collect(Collectors.joining(System.lineSeparator()));
        return new MvelCompilerUnit(MVEL.compileExpression(result), this);
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
