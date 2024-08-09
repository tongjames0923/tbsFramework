package tbs.framework.auth.utils;

import cn.hutool.core.text.AntPathMatcher;

/**
 * @author abstergo
 */
public class PathUtil {
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final boolean match(String url, String pattern) {
        return antPathMatcher.match(pattern, url);
    }
}
