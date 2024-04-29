package tbs.framework.base.utils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public enum TimeUtil {
    ;
    public static final String DATE_FORMAT_yyyyMMddHHmmss_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_yyyyMMddHHmm_STR = "yyyy-MM-dd HH:mm";
    public static final SimpleDateFormat DATE_FORMAT_yyyyMMddHHmmss =
        new SimpleDateFormat(TimeUtil.DATE_FORMAT_yyyyMMddHHmmss_STR);
    public static final SimpleDateFormat DATE_FORMAT_yyyyMMddHHmm = new SimpleDateFormat(
        TimeUtil.DATE_FORMAT_yyyyMMddHHmm_STR);
    public static final DateTimeFormatter DATE_FORMAT_TEMPORAL_yyyyMMddHHmmss =
        DateTimeFormatter.ofPattern(TimeUtil.DATE_FORMAT_yyyyMMddHHmmss_STR);

    public static final DateTimeFormatter DATE_FORMAT_TEMPORAL_yyyyMMddHHmm =
        DateTimeFormatter.ofPattern(TimeUtil.DATE_FORMAT_yyyyMMddHHmm_STR);
}
