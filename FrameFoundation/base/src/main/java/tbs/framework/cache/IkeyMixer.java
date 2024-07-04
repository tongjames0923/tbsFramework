package tbs.framework.cache;

import cn.hutool.core.util.StrUtil;

/**
 * @author Abstergo
 */
public interface IkeyMixer {
    /**
     * 键混淆
     * @param key
     * @return
     */
    public default String mixKey(String key) {
        return StrUtil.isEmpty(key) ? "null" : key;
    }
}
