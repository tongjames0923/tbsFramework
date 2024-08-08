package tbs.framework.log.impls;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.utils.LogFactory;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.proxy.IAutoProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author abstergo
 */
public class AutoLoggerImpl implements IAutoProxy {

    private String factoryBean = "";
    private String loggerName = "";

    private ILogger logger = null;

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    @Override
    public void wiredValue(Field field, Object target) {

        factoryBean = BeanNameConstant.BUILTIN_LOGGER;
        loggerName = target.getClass().getPackageName() + "." + target.getClass().getSimpleName();
        AutoLogger autoLogger = field.getDeclaredAnnotation(AutoLogger.class);
        if (autoLogger != null) {
            if (!StrUtil.isEmpty(autoLogger.factory())) {
                factoryBean = autoLogger.factory();
            }
            if (!StrUtil.isEmpty(autoLogger.value())) {
                loggerName = autoLogger.value();
            }
        }

    }

    @Override
    public Object proxyExecute(Object proxy, Method method, Object[] args) throws Throwable {
        if (isInit.compareAndSet(false, true)) {
            synchronized (this) {
                logger = SpringUtil.getBean(factoryBean, LogFactory.class).getLogger(loggerName);
            }
        }

        // 确保 logger 已经初始化
        if (logger == null) {
            throw new IllegalStateException("Logger not initialized.");
        }

        return method.invoke(logger, args);

    }

    @Override
    public Class<?> requiredType() {
        return ILogger.class;
    }
}
