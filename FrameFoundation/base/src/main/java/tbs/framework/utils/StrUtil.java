package tbs.framework.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

/**
 * StrUtil是一个工具类，提供了许多用于处理字符串的方法。
 *
 * @author abstergo
 */
public class StrUtil extends StringUtils {

    /**
     * 从输入流中读取字符串。
     *
     * @param str 输入流
     * @return 读取到的字符串
     */
    public static String readStreamAsString(InputStream str) {
        return readStreamAsString(str, Charset.defaultCharset());
    }

    /**
     * 从输入流中读取字符串。
     *
     * @param str     输入流
     * @param charset 字符编码
     * @return 读取到的字符串
     */
    public static String readStreamAsString(InputStream str, Charset charset) {
        return new BufferedReader(new InputStreamReader(str, charset)).lines()
            .collect(Collectors.joining(System.lineSeparator()));
    }
}