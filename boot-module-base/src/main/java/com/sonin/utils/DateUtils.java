package com.sonin.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
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

}
