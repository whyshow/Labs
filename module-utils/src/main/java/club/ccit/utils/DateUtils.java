package club.ccit.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 一个日期处理类
 * version: 1.0
 */
public class DateUtils {

    /**
     * 将日期对象格式化为指定格式的字符串
     *
     * @param date    要格式化的日期对象
     * @param pattern 日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date, DatePattern pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        SimpleDateFormat sdf = getDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 将日期字符串解析为日期对象
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 解析后的日期对象
     */
    public static Date parseDate(String dateStr, DatePattern pattern) {
        if (dateStr == null || pattern == null) {
            return null;
        }
        SimpleDateFormat sdf = getDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前日期和时间的字符串表示
     *
     * @param pattern 日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 当前日期和时间的字符串表示
     */
    public static String getCurrentDate(DatePattern pattern) {
        return formatDate(new Date(), pattern);
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 两个日期之间的天数差
     */
    public static long getDaysDifference(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        long diff = endDate.getTime() - startDate.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }

    /**
     * 将时间戳转换为指定格式的日期字符串
     *
     * @param timestamp 时间戳（毫秒）
     * @param pattern   日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    public static String timestampToDate(long timestamp, DatePattern pattern) {
        Date date = new Date(timestamp);
        return formatDate(date, pattern);
    }

    /**
     * 将日期字符串转换为时间戳
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式，例如 "yyyy-MM-dd HH:mm:ss"
     * @return 时间戳（毫秒）
     * @throws ParseException 如果解析失败抛出异常
     */
    public static long dateToTimestamp(String dateStr, DatePattern pattern) {
        Date date = parseDate(dateStr, pattern);
        return date != null ? date.getTime() : 0;
    }

    /**
     * 将指定日期向前或向后推指定天数，并以列表形式返回推移过程中的日期
     *
     * @param dateStr 指定日期字符串，格式需与 pattern 一致
     * @param days    要推移的总天数，正数表示向后推，负数表示向前推
     * @param pattern 日期格式，例如 "yyyy-MM-dd"
     * @return 推移过程中的日期字符串列表
     */
    public static List<String> shiftDateList(String dateStr, int days, DatePattern pattern) {
        SimpleDateFormat sdf = getDateFormat(pattern);
        Date date;
        try {
            date = sdf.parse(dateStr == null ? getCurrentDate(pattern) : dateStr.isEmpty() ? getCurrentDate(pattern) : dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        List<String> dateList = new ArrayList<>();
        int currentDays = 0;
        int incrementField = switch (pattern) {
            case YYYY -> Calendar.YEAR;
            case YYYY_MM -> Calendar.MONTH;
            default -> Calendar.DAY_OF_MONTH;
        };
        while (Math.abs(currentDays) <= Math.abs(days)) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(incrementField, days < 1 ? -1 : 1);
            currentDays += 1;
        }
        return dateList;
    }

    /**
     * 获取指定范围日期，输入起始日期和截止日期获取中间的日期列表
     *
     * @param startDateStr 起始日期字符串，格式需与 pattern 一致
     * @param endDateStr   截止日期字符串，格式需与 pattern 一致
     * @param pattern      日期格式，例如 "yyyy-MM-dd"
     * @return 包含指定范围内所有日期的字符串列表
     */
    public static List<String> getDateRange(String startDateStr, String endDateStr, DatePattern pattern) {
        SimpleDateFormat sdf = getDateFormat(pattern);
        Date startDate;
        Date endDate;
        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int incrementField = switch (pattern) {
            case YYYY -> Calendar.YEAR;
            case YYYY_MM -> Calendar.MONTH;
            default -> Calendar.DAY_OF_MONTH;
        };
        while (!calendar.getTime().after(endDate)) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(incrementField, 1);
        }
        return dateList;
    }

    public static SimpleDateFormat getDateFormat(DatePattern pattern) {
        return new SimpleDateFormat(pattern.getPattern());
    }

    /**
     * 定义常见日期格式的枚举类型
     */
    public enum DatePattern {
        YYYY("yyyy"),
        YYYY_MM("yyyy-MM"),
        YYYY_MM_DD("yyyy-MM-dd"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
        YYYYMM("yyyyMM"),
        YYYYMMDD("yyyyMMdd"),
        YYYYMMDDHH("yyyyMMddHH"),
        YYYYMMDDHHMM("yyyyMMddHHmm"),
        YYYYMMDDHHMMSS("yyyyMMddHHmmss");

        private final String pattern;

        DatePattern(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

}
