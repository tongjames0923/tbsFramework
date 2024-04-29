package tbs.framework.base.utils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static final String DATE_FORMAT_yyyyMMddHHmmss_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_yyyyMMddHHmm_STR = "yyyy-MM-dd HH:mm";
    public static final SimpleDateFormat DATE_FORMAT_yyyyMMddHHmmss =
        new SimpleDateFormat(DATE_FORMAT_yyyyMMddHHmmss_STR);
    public static final SimpleDateFormat DATE_FORMAT_yyyyMMddHHmm = new SimpleDateFormat(DATE_FORMAT_yyyyMMddHHmm_STR);
    public static final DateTimeFormatter DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss =
        DateTimeFormatter.ofPattern(DATE_FORMAT_yyyyMMddHHmmss_STR);

    public static final DateTimeFormatter DATE_FORMAT_TEMPORAL_yyyyMMddHHmm =
        DateTimeFormatter.ofPattern(DATE_FORMAT_yyyyMMddHHmm_STR);
}
