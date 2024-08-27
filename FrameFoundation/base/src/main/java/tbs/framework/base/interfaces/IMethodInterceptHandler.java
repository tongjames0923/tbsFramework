package tbs.framework.base.interfaces;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * 定义一个方法拦截处理器接口，用于处理方法调用过程中的参数、异常和返回值。
 */
public interface IMethodInterceptHandler extends Ordered {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static final class HandleReturnedResult {
        private Object result;
        private boolean isFinal;
        private Throwable error;

        public static final HandleReturnedResult finalResult(Object result) {
            return new HandleReturnedResult(result, true, null);
        }

        public static final HandleReturnedResult result(Object result) {
            return new HandleReturnedResult(result, false, null);
        }

    }

    /**
     * 处理方法调用过程中的参数。
     *
     * @param target 目标对象
     * @param method 方法对象
     * @param args   方法参数
     * @return 处理后的参数
     */
    Object[] handleArgs(@NotNull Object target, @NotNull Method method, @NotNull Object... args);

    /**
     * 处理方法调用过程中的异常。
     *
     * @param e      异常对象
     * @param target 目标对象
     * @param method 方法对象
     * @param args   方法参数
     */
    void handleException(@NotNull Throwable e, @NotNull Object target, @NotNull Method method, @NotNull Object... args);

    /**
     * 处理方法调用过程中的返回值。
     *
     * @param target 目标对象
     * @param method 方法对象
     * @param result 返回值对象
     * @param args   方法参数
     * @return 处理后的返回值
     */
    HandleReturnedResult handleReturn(@NotNull Object target, @NotNull Method method, Object result,
        @NotNull Object... args);

    @Override
    default int getOrder() {
        return 0;
    }
}

