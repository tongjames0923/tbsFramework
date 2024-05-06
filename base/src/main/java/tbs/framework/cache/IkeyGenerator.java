package tbs.framework.cache;

import cn.hutool.core.util.StrUtil;

public interface IkeyGenerator {
    public default String generateKey(String key) {
        return StrUtil.isEmpty(key) ? "null" : key;
    }
}
