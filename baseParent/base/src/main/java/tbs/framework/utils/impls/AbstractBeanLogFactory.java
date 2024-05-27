package tbs.framework.utils.impls;

import cn.hutool.extra.spring.SpringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tbs.framework.cache.IkeyMixer;
import tbs.framework.log.ILogger;
import tbs.framework.utils.BeanUtil;
import tbs.framework.base.utils.LogFactory;

/**
 * 通过SpringBean管理的日志
 *
 * @author Abstergo
 */
public abstract class AbstractBeanLogFactory extends LogFactory implements IkeyMixer {

    /**
     * 生成新的日志器
     *
     * @param name
     * @return
     */
    protected abstract ILogger newLogger(String name);

    @Override
    public String mixKey(String key) {
        return "LOGGER-" + key;
    }

    @NotNull
    @Override
    public ILogger getLogger(@Nullable String name) {
        String beanName = mixKey(name);
        if (SpringUtil.getApplicationContext().containsBean(mixKey(name))) {
            return SpringUtil.getBean(mixKey(name), ILogger.class);
        } else {
            ILogger logger = newLogger(name);
            BeanUtil.registerBean(logger, beanName);
            return logger;
        }
    }
}
