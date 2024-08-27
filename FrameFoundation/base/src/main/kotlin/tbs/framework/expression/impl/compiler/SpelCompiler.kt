package tbs.framework.expression.impl.compiler

import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import tbs.framework.expression.*
import tbs.framework.expression.impl.version.SimpleVersion
import tbs.framework.expression.impl.version.checker.SinceVersionChecker
import tbs.framework.utils.StrUtil
import java.io.InputStream

/**
 * SPEL表达式编译器
 *
 */
class SpelCompiler : IExpressionCompiler {
    // 创建spel表达式分析器
    private var parser: ExpressionParser = SpelExpressionParser()
    override fun getCompilerUnit(code: InputStream): ICompilerUnit {

        return object : ICompilerUnit {
            val ep = parser.parseExpression(StrUtil.readStreamAsString(code));
            override fun exeCompilerUnit(context: IExpressionContext?, vararg args: Any?): ICompilerUnit.ExecuteResult {
                var realContext = StandardEvaluationContext()
                if (context != null) {
                    for (item in context.variablesMap.entries) {
                        realContext.setVariable(item.key, item.value)
                    }
                }
                realContext.setVariable("args", args)
                return ICompilerUnit.ExecuteResult(ep.getValue(realContext));
            }

            override fun compiler(): IExpressionCompiler {
                return this@SpelCompiler
            }
        }
    }

    override fun getVersion(): IVersion {
        return SimpleVersion(1, 0, 0);
    }

    override fun getVersionChecker(): IVersionChecker {
        return SinceVersionChecker(1, 0);
    }
}