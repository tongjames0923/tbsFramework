package tbs.framework.sql.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotatedElementUtils;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.sql.annotations.SqlAutoValue;
import tbs.framework.sql.interfaces.IAutoValueProvider;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * SqlAutoFillIntercepter 是一个 MyBatis 插件，用于在执行 SQL 语句之前自动填充对象的字段值。
 * </p>
 *
 * <p>
 * 该插件通过拦截 ParameterHandler 的 setParameters 方法，在设置参数之前，自动填充对象的字段值。
 * </p>
 *
 * @author abstergo
 */
@Intercepts(@Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class}))
public class SqlAutoFillIntercepter implements Interceptor {

    @AutoLogger
    private ILogger logger;

    /**
     * 拦截器方法，在设置参数之前，自动填充对象的字段值。
     *
     * @param invocation MyBatis 插件调用对象
     * @return 原始操作的结果
     * @throws Throwable 如果发生异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取参数处理器实例
        ParameterHandler parameterHandler = (ParameterHandler)invocation.getTarget();
        // 在设置参数之前，可以访问参数并做一些操作，例如打印参数信息
        Object parameters = parameterHandler.getParameterObject();
        if (parameters instanceof Map) {
            ((Map<?, ?>)parameters).values().stream().distinct().forEach((o) -> {
                applyValue(o);
            });
        } else {
            applyValue(parameters);
        }
        // 继续执行原始操作
        Object result = invocation.proceed();

        // 如果需要，可以在这里对结果进行操作
        return result;
    }

    /**
     * 自动填充对象的字段值。
     *
     * @param o 需要自动填充的对象
     */
    @NotNull
    private void applyValue(Object o) {
        Class<?> cas = o.getClass();
        for (Field field : cas.getDeclaredFields()) {
            Set<SqlAutoValue> f = AnnotatedElementUtils.getAllMergedAnnotations(field, SqlAutoValue.class);
            if (CollUtil.isEmpty(f)) {
                continue;
            }
            field.setAccessible(true);
            for (SqlAutoValue sqlAutoValue : f) {
                try {
                    IAutoValueProvider valueProvider = SpringUtil.getBean(sqlAutoValue.value());
                    if (valueProvider.support(field.getType(), field.get(o))) {
                        field.set(o, valueProvider.getValue(o, field));
                        logger.debug("自动填充字段{}，值为{}", field.getName(), field.get(o));
                    }
                } catch (Exception e) {
                    logger.debug("自动填充失败，请检查配置.{}", e);
                }
            }

        }

    }

    /**
     * 创建代理对象，用于拦截目标对象。
     *
     * @param o 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    /**
     * 设置插件的属性。
     *
     * @param properties 插件属性
     */
    @Override
    public void setProperties(Properties properties) {

    }
}
