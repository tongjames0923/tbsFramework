package tbs.framework.mvel2;

import org.mvel2.MVEL;
import tbs.framework.expression.ICompilerUnit;
import tbs.framework.expression.IExpressionCompiler;
import tbs.framework.expression.IExpressionContext;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @author Abstergo
 */
public class MvelCompilerUnit implements ICompilerUnit {
    Serializable object = null;

    IExpressionCompiler compiler;

    public MvelCompilerUnit(Serializable obj, Mvel2Compiler compiler) {
        this.compiler = compiler;
        this.object = obj;
    }

    /**
     * 执行编译单元
     *
     * @param context 表达式上下文
     * @param args    未实现，请用上下文传递参数
     * @return
     * @throws Exception
     */
    @Override
    public ExecuteResult exeCompilerUnit(IExpressionContext context, Object... args) throws Exception {
        return new ExecuteResult(MVEL.executeExpression(object, context.getVariablesMap()));
    }

    @Override
    public IExpressionCompiler compiler() {
        return compiler;
    }
}
