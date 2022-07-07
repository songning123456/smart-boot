package com.sonin.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <pre>
 * 日期工具类
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/20 17:22
 */
public class DateUtils {

    /**
     * 根据(开始时间, 结束时间)获取每一个时间间隔的数据, 主要用于eCharts图标xData
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     */
    public static List<String> interval(String startTime, String endTime, String format, int field, int amount) {
        List<String> intervalList = new ArrayList<>();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);
            List<Date> dateList = new ArrayList<>();
            dateList.add(startDate);
            Calendar calendarOfStart = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calendarOfStart.setTime(startDate);
            Calendar calendarOfEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calendarOfEnd.setTime(endDate);
            // 测试此日期是否在指定日期之后
            while (endDate.compareTo(calendarOfStart.getTime()) >= 0) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calendarOfStart.add(field, amount);
                if (endDate.compareTo(calendarOfStart.getTime()) >= 0 && !dateList.contains(calendarOfStart.getTime())) {
                    dateList.add(calendarOfStart.getTime());
                }
            }
            for (Date item : dateList) {
                intervalList.add(simpleDateFormat.format(item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intervalList;
    }

    public static List<String> intervalByHour(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.HOUR_OF_DAY, 1);
    }

    public static List<String> intervalByDay(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.DAY_OF_MONTH, 1);
    }

    public static List<String> intervalByMonth(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.MONTH, 1);
    }

    public static List<String> intervalByYear(String startTime, String endTime, String format) {
        return interval(startTime, endTime, format, Calendar.YEAR, 1);
    }

    /**
     * 根据某个时间获取前多少天，或者后多少天
     *
     * @param dateTime yyyy-MM-dd
     * @param limit    天数
     * @return
     */
    public static List<String> increaseDay(String dateTime, int limit) {
        return stepDay(dateTime, limit, true);
    }

    /**
     * 根据某个时间获取前多少天，或者后多少天
     *
     * @param dateTime yyyy-MM-dd
     * @param limit    天数
     * @return
     */
    public static List<String> decreaseDay(String dateTime, int limit) {
        return stepDay(dateTime, limit, false);
    }

    private static List<String> stepDay(String dateTime, int limit, boolean flag) {
        LinkedList<String> dateList = new LinkedList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int factor = 1;
        if (!flag) {
            factor = -1;
        }
        for (int i = 0; i < limit; i++) {
            Date date = simpleDateFormat.parse(dateTime, new ParsePosition(0));
            calendar.setTime(date);
            calendar.add(Calendar.DATE, factor * i);
            if (flag) {
                dateList.addLast(simpleDateFormat.format(calendar.getTime()));
            } else {
                dateList.addFirst(simpleDateFormat.format(calendar.getTime()));
            }
        }
        return dateList;
    }


    /**
     * <pre>
     * 根据某个日期获取指定时间
     * </pre>
     *
     * @param currentDate
     * @param field:      Calendar.DATE、Calendar.MONTH ...
     * @param step:       step>0获取往后某一天，step<0获取之前某一天
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date someDate(Date currentDate, Integer field, Integer step) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(field, calendar.get(field) + step);
        return calendar.getTime();
    }

    /**
     * <pre>
     * 昨天此刻
     * </pre>
     *
     * @param currentDate
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date prevDay(Date currentDate) {
        return someDate(currentDate, Calendar.DATE, -1);
    }

    /**
     * <pre>
     * 上个月此刻
     * </pre>
     *
     * @param currentDate
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date prevMonth(Date currentDate) {
        return someDate(currentDate, Calendar.MONTH, -1);
    }

    /**
     * <pre>
     * 去年此刻
     * </pre>
     *
     * @param currentDate
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date prevYear(Date currentDate) {
        return someDate(currentDate, Calendar.YEAR, -1);
    }

    /**
     * <pre>
     * 明天此刻
     * </pre>
     *
     * @param currentDate
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date nextDay(Date currentDate) {
        return someDate(currentDate, Calendar.DATE, 1);
    }

    /**
     * <pre>
     * 下个月此刻
     * </pre>
     *
     * @param currentDate
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date nextMonth(Date currentDate) {
        return someDate(currentDate, Calendar.MONTH, 1);
    }

    /**
     * <pre>
     * 明年此刻
     * </pre>
     *
     * @param currentDate
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static Date nextYear(Date currentDate) {
        return someDate(currentDate, Calendar.YEAR, 1);
    }

    /**
     * <pre>
     * 获取某一周: 0:这周; -1:上一周; 1:下一周;
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static List<String> someWeek(int n) {
        String format = "yyyy-MM-dd";
        // 获取本周
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        // 本周周一
        String startTime = LocalDateTime.now().minusDays(7 - dayOfWeek.getValue()).format(DateTimeFormatter.ofPattern(format));
        // 本周周日
        String endTime = LocalDateTime.now().plusDays(7 - dayOfWeek.getValue()).format(DateTimeFormatter.ofPattern(format));
        if (n != 0) {
            startTime = date2Str(someDate(strToDate(startTime, format), Calendar.DATE, n * 7), format);
            endTime = date2Str(someDate(strToDate(endTime, format), Calendar.DATE, n * 7), format);
        }
        return intervalByDay(startTime, endTime, format);
    }

    /**
     * <pre>
     * 获取某一个月: 0:这个月; -1:上一月; 1:下一月;
     * </pre>
     *
     * @param
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static List<String> someMonth(int n) {
        Date date = someDate(new Date(), Calendar.MONTH, n);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR); //年份
        int month = calendar.get(Calendar.MONTH) + 1; //月份
        int day = calendar.getActualMaximum(Calendar.DATE); // 天数
        String startTime = year + "-" + (month < 10 ? ("0" + month) : month) + "-01";
        String endTime = year + "-" + (month < 10 ? ("0" + month) : month) + "-" + day;
        return intervalByDay(startTime, endTime, "yyyy-MM-dd");
    }

    /**
     * String => java.util.Date
     *
     * @param str        表示日期的字符串
     * @param dateFormat 传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
     * @return java.util.Date类型日期对象（如果转换失败则返回null）
     */
    public static Date strToDate(String str, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = simpleDateFormat.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * <pre>
     *  java.util.Date => String
     * </pre>
     *
     * @param date
     * @param format
     * @author sonin
     * @Description: TODO(这里描述这个方法的需求变更情况)
     */
    public static String date2Str(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) {
        // System.out.println(someWeek(-1));
        System.out.println(someMonth(1));
        System.out.println("");
    }

}
